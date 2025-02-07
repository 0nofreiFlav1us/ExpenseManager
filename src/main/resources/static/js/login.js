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
