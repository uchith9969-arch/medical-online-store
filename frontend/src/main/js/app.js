
const popup = document.getElementById("paymentPopup");
const closePopup = document.getElementById("closePopup");

const payButtons = document.querySelectorAll(".pay-btn");

const cashBtn = document.getElementById("cashBtn");
const cardBtn = document.getElementById("cardBtn");

const cardForm = document.getElementById("cardForm");
const message = document.getElementById("message");

// OPEN POPUP

payButtons.forEach(button => {

  button.addEventListener("click", () => {

    popup.style.display = "flex";

  });

});

// CLOSE POPUP

closePopup.addEventListener("click", () => {

  popup.style.display = "none";

  resetPopup();

});

// CASH PAYMENT

cashBtn.addEventListener("click", () => {

  cardForm.classList.add("hidden");

  message.innerHTML = "Cash Payment Completed ✅";

});

// SHOW CARD FORM

cardBtn.addEventListener("click", () => {

  cardForm.classList.remove("hidden");

  message.innerHTML = "";

});

// CARD PAYMENT

cardForm.addEventListener("submit", (e) => {

  e.preventDefault();

  message.innerHTML = "Card Payment Successful ✅";

  cardForm.reset();

  setTimeout(() => {

    popup.style.display = "none";

    resetPopup();

  }, 2000);

});

// RESET

function resetPopup(){

  cardForm.classList.add("hidden");

  message.innerHTML = "";

}