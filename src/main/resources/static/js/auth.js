//validate sign up
function validateSignUpForm() {
  const password = document.getElementById("password").value;
  const confirm = document.getElementById("confirmPassword").value;

  if (password !== confirm) {
    alert("Password and Confirm Password do not match.");
    return false;
  }

  return true;
}
//handle signin
function handleSignIn() {
  const username = document.getElementById("username").value.trim();
  const password = document.getElementById("password").value.trim();

  if (username === "" || password === "") {
    alert("Please enter both username and password.");
    return false;
  }

  // Optionally add further validation here (e.g., password length)

  // Submit the form manually
  document.getElementById("loginForm").submit();

  return true;
}
