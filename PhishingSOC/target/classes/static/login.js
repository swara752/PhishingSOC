// Login handling: POST credentials to /api/auth/login and store JWT
document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('loginForm');
  const userInput = document.getElementById('userInput');
  const passInput = document.getElementById('password');
  const flowInput = document.getElementById('flow');
  const sessionInfo = document.getElementById('sessionInfo');

  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = userInput.value;
    const password = passInput.value;
    const flow = flowInput.value || 'user';

    sessionInfo.textContent = `Signing in ${email}...`;

    try {
      const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) {
        const err = await res.json().catch(()=>({ error: 'Unknown error' }));
        sessionInfo.textContent = `Login failed`;
        alert(err.error || err.message || 'Login failed');
        return;
      }

      const data = await res.json();
      // store token and email in localStorage
      if (data.token) {
        localStorage.setItem('phish_token', data.token);
        localStorage.setItem('phish_email', data.email || email);
        localStorage.setItem('phish_flow', flow);
      }

      sessionInfo.textContent = `Signed in as ${data.email || email}`;
      // redirect to dashboard
      window.location.href = 'dashboard.html';
    } catch (err) {
      sessionInfo.textContent = 'Network error';
      alert('Network error: ' + err.message);
    }
  });

  // Optional: auto-fill from localStorage if already logged in
  const savedEmail = localStorage.getItem('phish_email');
  if (savedEmail) {
    userInput.value = savedEmail;
  }
});

// Helper function for other pages to make authenticated API calls
window.apiCall = async function(endpoint, options = {}) {
  const token = localStorage.getItem('phish_token');
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return fetch(endpoint, {
    ...options,
    headers
  });
};

