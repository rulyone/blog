package dev.quantumentangled.blog.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import dev.quantumentangled.blog.entities.MailQueue;
import dev.quantumentangled.blog.repositories.MailQueueRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MailQueueService {
    
    private static final Logger logger = LoggerFactory.getLogger(MailQueueService.class);
    
    private MailQueueRepository repository;
    
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String configuredMail;

    public MailQueueService(MailQueueRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }
    
    public MailQueue queueMail(String to, String subject, String body) {
        MailQueue email = new MailQueue();
        email.setToAddress(to);
        email.setSubject(sanitizeForEmail(subject));
        email.setBody(sanitizeForEmail(body));
        email.setStatus(MailQueue.Status.PENDING);
        return repository.save(email);
    }
    
    @Scheduled(fixedDelay = 60000) // Every minute
    void processMailQueue() {
        List<MailQueue> pendingEmails = repository.findByStatusAndAttemptsLessThan(
            MailQueue.Status.PENDING, 3
        );
        
        for (MailQueue email : pendingEmails) {
            processMail(email);
        }
    }
    
    private void processMail(MailQueue email) {
        
        try {
            sendMail(email);
            email.setStatus(MailQueue.Status.SENT);
            email.setSentAt(LocalDateTime.now());
            email.setErrorMessage(null);
        } catch (MailException e) {
            handleMailFailure(email, e);
        }
        
        repository.save(email);
    }
    
    private void handleMailFailure(MailQueue email, MailException e) {
        email.setAttempts(email.getAttempts() + 1);
        email.setErrorMessage(e.getMessage());
        
        if (email.getAttempts() >= 3) {
            email.setStatus(MailQueue.Status.FAILED);
            logger.error("Email permanently failed after {} attempts: {}", 
                     email.getAttempts(), email.getId());
        } else {
            email.setStatus(MailQueue.Status.PENDING);
            logger.warn("Email temporarily failed, attempt {}: {}", 
                    email.getAttempts(), email.getId());
        }
    }
    
    private void sendMail(MailQueue email) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getToAddress());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        message.setFrom(configuredMail);
        mailSender.send(message);
    }
    
    private String sanitizeForEmail(String input) {
        if (input == null) return "";
        
        // Remove newlines that could be used for header injection
        String cleaned = input.replaceAll("[\r\n]", " ");
        
        // Remove potential email headers
        cleaned = cleaned.replaceAll("(?i)(content-type:|bcc:|cc:|to:|from:)", "");
        
        // Escape special characters
        return HtmlUtils.htmlEscape(cleaned);
    }
}