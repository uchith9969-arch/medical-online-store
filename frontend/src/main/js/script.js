const API_URL = "http://localhost:8080/api/reviews";

/* PAGE LOAD */

window.onload = function () {
    loadReviews();
};

/* LOAD REVIEWS */

function loadReviews() {

    fetch(API_URL)

        .then(response => response.json())

        .then(data => {

            let container =
                document.getElementById("list-container");

            container.innerHTML = "";

            data.forEach(review => {

                container.innerHTML += `

                    <div class="review-item">

                        <b>${review.name}</b>

                        (Rating: ${review.rating}/5)

                        <p>${review.comment}</p>

                    </div>

                `;
            });

        })

        .catch(error => {
            console.log(error);
        });
}

/* ADD REVIEW */

function addReview() {

    let name =
        document.getElementById("name").value;

    let rating =
        document.getElementById("rating").value;

    let comment =
        document.getElementById("comment").value;

    fetch(API_URL, {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify({
            name: name,
            rating: rating,
            comment: comment
        })

    })

    .then(() => {

        alert("Review Added!");

        loadReviews();

        document.getElementById("name").value = "";
        document.getElementById("rating").value = "";
        document.getElementById("comment").value = "";

    })

    .catch(error => {
        console.log(error);
    });
}