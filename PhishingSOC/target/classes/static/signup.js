// Signup form handling with password strength validation
document.addEventListener('DOMContentLoaded', () => {
  const signupForm = document.getElementById('signupForm');
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const confirmPasswordInput = document.getElementById('confirmPassword');
  const strengthFill = document.getElementById('strengthFill');
  const strengthText = document.getElementById('strengthText');
  const formError = document.getElementById('formError');
  const formSuccess = document.getElementById('formSuccess');
  const emailError = document.getElementById('emailError');
  const passwordError = document.getElementById('passwordError');
  const confirmError = document.getElementById('confirmError');
  const resetBtn = document.getElementById('resetBtn');

  // Password strength calculator
  function calculateStrength(pwd) {
    let strength = 0;
    if (pwd.length >= 8) strength += 1;
    if (pwd.length >= 12) strength += 1;
    if (/[a-z]/.test(pwd)) strength += 1;
    if (/[A-Z]/.test(pwd)) strength += 1;
    if (/[0-9]/.test(pwd)) strength += 1;
    if (/[^a-zA-Z0-9]/.test(pwd)) strength += 1;
    return Math.min(strength, 4);
  }

  // Update strength indicator
  passwordInput.addEventListener('input', () => {
    const pwd = passwordInput.value;
    const strength = calculateStrength(pwd);
    const percentages = [0, 25, 50, 75, 100];
    const colors = ['', 'strength-weak', 'strength-fair', 'strength-good', 'strength-good'];
    const labels = ['', 'Weak', 'Fair', 'Good', 'Strong'];

    strengthFill.style.width = percentages[strength] + '%';
    strengthFill.className = 'strength-fill ' + colors[strength];
    strengthText.textContent = pwd.length > 0 ? labels[strength] : '';

    // Clear error when user fixes password
    if (pwd.length >= 8) {
      passwordError.textContent = '';
    }
  });

  // Real-time confirmation check
  confirmPasswordInput.addEventListener('input', () => {
    if (confirmPasswordInput.value && passwordInput.value !== confirmPasswordInput.value) {
      confirmError.textContent = 'Passwords do not match';
    } else {
      confirmError.textContent = '';
    }
  });

  // Real-time email validation
  emailInput.addEventListener('blur', () => {
    const email = emailInput.value;
    if (email && !email.includes('@')) {
      emailError.textContent = 'Invalid email format';
    } else {
      emailError.textContent = '';
    }
  });

  // Toggle password visibility
  document.getElementById('togglePwd1').addEventListener('click', () => {
    if (passwordInput.type === 'password') {
      passwordInput.type = 'text';
      document.getElementById('togglePwd1').textContent = '(hide)';
    } else {
      passwordInput.type = 'password';
      document.getElementById('togglePwd1').textContent = '(show)';
    }
  });

  document.getElementById('togglePwd2').addEventListener('click', () => {
    if (confirmPasswordInput.type === 'password') {
      confirmPasswordInput.type = 'text';
      document.getElementById('togglePwd2').textContent = '(hide)';
    } else {
      confirmPasswordInput.type = 'password';
      document.getElementById('togglePwd2').textContent = '(show)';
    }
  });

  // Reset form
  resetBtn.addEventListener('click', () => {
    signupForm.reset();
    strengthFill.style.width = '0%';
    strengthFill.className = 'strength-fill';
    strengthText.textContent = '';
    formError.textContent = '';
    formSuccess.textContent = '';
    emailError.textContent = '';
    passwordError.textContent = '';
    confirmError.textContent = '';
  });

  // Form submission
  signupForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Clear previous messages
    formError.textContent = '';
    formSuccess.textContent = '';
    emailError.textContent = '';
    passwordError.textContent = '';
    confirmError.textContent = '';

    // Validation
    const email = emailInput.value.trim();
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (!email || !email.includes('@')) {
      emailError.textContent = 'Please enter a valid email';
      return;
    }

    if (password.length < 8) {
      passwordError.textContent = 'Password must be at least 8 characters';
      return;
    }

    if (password !== confirmPassword) {
      confirmError.textContent = 'Passwords do not match';
      return;
    }

    // Submit registration
    try {
      const res = await window.apiCall('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      });

      if (!res.ok) {
        const err = await res.json().catch(() => ({ error: 'Registration failed' }));
        formError.textContent = err.error || err.message || 'Registration failed';
        return;
      }

      const data = await res.json();
      formSuccess.textContent = 'Account created successfully! Redirecting to login...';
      
      // Store credentials for auto-fill on login
      sessionStorage.setItem('newuser_email', email);
      
      setTimeout(() => {
        window.location.href = 'login.html';
      }, 1500);

    } catch (err) {
      formError.textContent = 'Network error: ' + err.message;
    }
  });
});
