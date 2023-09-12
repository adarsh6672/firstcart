function validateForm() {
  var name = document.getElementById("name").value;
  var email = document.getElementById("email").value;
  var phone = document.getElementById("phone").value;
  var password = document.getElementById("password").value;
  var confirmPassword = document.getElementById("confirm").value;

  var nameRegex = /^[a-zA-Z ]{2,30}$/;
  var emailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
  var phoneRegex = /^\d{10}$/;
  var passwordRegex = /^[a-zA-Z0-9!@#$%^&*()_+|\-=?;:'",.<>{}[\]\\/]{6,}$/;

  if (!nameRegex.test(name)) {
    alert("Please enter a valid name.");
    return false;
  }

  if (!emailRegex.test(email)) {
    alert("Please enter a valid email address.");
    return false;
  }

  if (!phoneRegex.test(phone)) {
    alert("Please enter a valid phone number.");
    return false;
  }

  if (!passwordRegex.test(password)) {
    alert("Please enter a valid password./ password length must be 6");
    return false;
  }
  if (password != confirmPassword) {
      alert("Error: Passwords do not match.");
      return false;
    }

  return true;
}

var form = document.getElementById("myForm");
form.addEventListener("submit", function(event) {
  event.preventDefault();
  validateForm();
});
