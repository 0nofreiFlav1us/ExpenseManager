function submitForm() {
    // Get the form element by its ID
    const form = document.getElementById("login-form");

    // Check if the form exists to avoid errors
    if (form) {
        form.submit(); // Submit the form programmatically
    } else {
        console.error("Form not found!");
    }
}

// This function will run when the form is submitted
function handleFormSubmit(event) {
    // Prevents the form from being submitted the default way (so we can handle it via JS)
    event.preventDefault();

    // Get the values from the input fields
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const passwordConfirmation = document.getElementById('password_confirmation').value;

    // Now you can log or use the values
    console.log('Username:', username);
    console.log('Password:', password);
    console.log('Confirm Password:', passwordConfirmation);

    // You can also check if the passwords match here
    if (password !== passwordConfirmation) {
        alert('Passwords do not match!');
        return;
    }

    event.target.submit();
}

// Add event listener to the form to intercept submission
document.querySelector('form').addEventListener('submit', handleFormSubmit);

