// Form submit handler
document.getElementById('signUpForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    // Get form data
    const user = {
        email: document.getElementById('email').value,
		name: document.getElementById('name').value,

        pwd: document.getElementById('pwd').value
    };

    // Send data using Axios
    axios
        .post('/signUp', user, { withCredentials: true })
        .then((res) => {
            console.log(res);
            if (res.data === true) {
                console.log(res.data)
                document.location.href = "/signIn"; // Redirect to home
            }
        })
        .catch((err) => {
            console.log(err);
        });
});