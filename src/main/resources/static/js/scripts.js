document.querySelectorAll('.local-date').forEach(el => {
    const date = new Date(el.dataset.time);
    // Format as "22 Jun 2025 14:45"
    const formatted = date.toLocaleString(undefined, {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
    el.textContent = formatted;
  });

document.body.addEventListener('htmx:afterRequest', function (evt) {
  const form = evt.detail.elt;
  if (form && form.matches('form.comment-form')) {
    form.reset();
  }
});

document.addEventListener('htmx:afterSettle', function(evt) {
    if (evt.detail.pathInfo?.requestPath?.includes('/posts')) {
        // Find all code blocks that haven't been processed by Prism yet
        const unprocessedCode = document.querySelectorAll('pre code:not(.token):not([data-prism-processed])');

        unprocessedCode.forEach(block => {
            // Mark as processed to avoid re-processing
            block.setAttribute('data-prism-processed', 'true');

            // Ensure it has a language class if missing
            if (!block.className.match(/language-\w+/)) {
                block.classList.add('language-plaintext');
            }

            // Highlight this specific block
            if (typeof Prism !== 'undefined') {
                Prism.highlightElement(block);
            }
        });
    }
});