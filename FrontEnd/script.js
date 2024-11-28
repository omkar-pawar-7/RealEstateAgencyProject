function showForm() {
    document.getElementById('formContainer').style.display = 'block';
}

function hideForm() {
    document.getElementById('formContainer').style.display = 'none';
}

document.getElementById("contactForm").addEventListener("submit", function(event) {
    event.preventDefault(); 
    
    const data = {
        name: document.getElementById("name").value,
        email: document.getElementById("email").value,
        mobileno: document.getElementById("mobileno").value 
    };

    fetch('http://localhost:8082/Agency/contact', { 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Something went wrong');
        }
    })
    .then(result => {
        alert(result.message);
    })
    .catch(error => {
        console.error('Error:', error);
        alert('There was an issue submitting your form.');
    });
});
