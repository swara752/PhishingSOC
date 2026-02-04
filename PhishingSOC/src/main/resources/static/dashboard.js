document.addEventListener('DOMContentLoaded', () => {
	const token = localStorage.getItem('phish_token');
	const email = localStorage.getItem('phish_email');
  
  if (!token) {
    // No token — redirect to login
    window.location.href = 'login.html';
    return;
  }

  // Show session info
  console.log('PhishingSOC dashboard — token present');
  const info = document.createElement('div');
  info.style.padding = '1rem';
  info.style.color = '#9ae6b4';
  info.style.marginBottom = '1rem';
  info.innerHTML = `<strong>Session:</strong> Signed in as <code>${email || 'unknown'}</code>`;
  document.body.prepend(info);

  // Example: Fetch user profile with token
  const profileBtn = document.createElement('button');
  profileBtn.textContent = 'Fetch Profile';
  profileBtn.style.cssText = 'padding: 0.5rem 1rem; background: #00ff6a; color: #000; border: none; border-radius: 4px; cursor: pointer; margin-top: 0.5rem;';
  info.appendChild(profileBtn);

  profileBtn.addEventListener('click', async () => {
    try {
      const res = await window.apiCall('/api/auth/me', { method: 'GET' });
      if (res.ok) {
        const data = await res.json();
        alert(`Profile retrieved: ${JSON.stringify(data)}`);
      } else {
        alert(`Error: ${res.status} ${res.statusText}`);
      }
    } catch (err) {
      alert(`Network error: ${err.message}`);
    }
  });

  // Example: Logout button
  const logoutBtn = document.createElement('button');
  logoutBtn.textContent = 'Logout';
  logoutBtn.style.cssText = 'padding: 0.5rem 1rem; background: transparent; border: 1px solid #ff6b6b; color: #ff6b6b; border-radius: 4px; cursor: pointer; margin-top: 0.5rem; margin-left: 0.5rem;';
  info.appendChild(logoutBtn);

  logoutBtn.addEventListener('click', async () => {
    try {
      const res = await window.apiCall('/api/auth/logout', { method: 'POST' });
      if (res.ok) {
        localStorage.removeItem('phish_token');
        localStorage.removeItem('phish_email');
        window.location.href = 'login.html';
      } else {
        alert(`Logout failed: ${res.status}`);
      }
    } catch (err) {
      alert(`Logout error: ${err.message}`);
    }
  });
