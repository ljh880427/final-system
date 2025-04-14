// Form submit handler
document.getElementById('signInForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    // Get form data
    const user = {
        email: document.getElementById('email').value,

        pwd: document.getElementById('pwd').value
    };

    // Send data using Axios
    axios
        .post('/signIn', user, { withCredentials: true })
        .then((res) => {
            console.log(res);
            if (res.data === true) {
                console.log(res.data)
                document.location.href = "/"; // Redirect to home
            }
        })
        .catch((err) => {
            console.log(err);
        });
});