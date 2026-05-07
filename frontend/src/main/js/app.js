const payBtn = document.getElementById("payBtn");
const popup = document.getElementById("paymentPopup");
const closePopup = document.getElementById("closePopup");

const cashBtn = document.getElementById("cashBtn");
const cardBtn = document.getElementById("cardBtn");

const cardForm = document.getElementById("cardForm");
const message = document.getElementById("message");

// Open popup
payBtn.addEventListener("click", () => {
  popup.style.display = "flex";
});

// Close popup
closePopup.addEventListener("click", () => {
  popup.style.display = "none";
  resetForm();
});

// Cash payment
cashBtn.addEventListener("click", () => {
  message.innerHTML = "Cash Payment Successful ✅";
  cardForm.classList.add("hidden");
});

// Show card form
cardBtn.addEventListener("click", () => {
  cardForm.classList.remove("hidden");
  message.innerHTML = "";
});

// Process card payment
cardForm.addEventListener("submit", (e) => {
  e.preventDefault();

  message.innerHTML = "Card Payment Successful ✅";

  cardForm.reset();

  setTimeout(() => {
    popup.style.display = "none";
    resetForm();
  }, 2000);
});

// Reset
function resetForm(){
  cardForm.classList.add("hidden");
  message.innerHTML = "";
}