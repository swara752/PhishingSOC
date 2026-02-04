document.addEventListener('submit', function(e){
  const form = e.target;
  if(form.tagName === 'FORM'){
    e.preventDefault();
    alert('Demo login — backend not configured.');
  }
});
