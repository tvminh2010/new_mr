document.addEventListener('DOMContentLoaded', function () {
  const input = document.querySelector('input[name="scannedSerial"]');
  const statusDiv = document.getElementById('status-message');

  if (!input) return;

  input.addEventListener('keypress', function (e) {
    if (e.key !== 'Enter') return;
    e.preventDefault();

    const serial = input.value.trim();
    if (!serial) return;

    fetch('/picking/scan-serial', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ serial })
    })
      .then(res => res.json())
      .then(data => {
        statusDiv.style.display = 'block';
        statusDiv.textContent = data.message || 'Đã xử lý';
        statusDiv.className = data.success ? 'text-success' : 'text-danger';

        input.value = '';
        setTimeout(() => input.focus(), 100);
      })
      .catch(console.error);
  });
});
