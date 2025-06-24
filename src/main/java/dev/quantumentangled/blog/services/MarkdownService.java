package dev.quantumentangled.blog.services;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;


@Service
public class MarkdownService {
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public String renderToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
