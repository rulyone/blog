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
