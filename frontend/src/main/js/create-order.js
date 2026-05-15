const API = "http://localhost:8080/api";

// ── Add a new item row ──
function addItemRow() {
    const container = document.getElementById("itemsContainer");
    const row = document.createElement("div");
    row.className = "item-row";
    row.innerHTML = `
        <input type="number" class="medicineId" placeholder="Medicine ID" min="1" />
        <input type="number" class="quantity qty" placeholder="Qty" min="1" value="1" />
        <button type="button" class="btn btn-sm btn-danger" onclick="this.parentElement.remove()">✕</button>
    `;
    container.appendChild(row);
}

// ── Toggle urgency fee note ──
function toggleFeeNote() {
    const type = document.getElementById("orderType").value;
    document.getElementById("feeNote").style.display = type === "URGENT" ? "block" : "none";
}

// ── Submit order ──
async function submitOrder() {
    const userId    = document.getElementById("userId").value.trim();
    const orderType = document.getElementById("orderType").value;

    if (!userId) { showError("User ID is required"); return; }

    // Collect items
    const medicineIds = document.querySelectorAll(".medicineId");
    const quantities  = document.querySelectorAll(".quantity");

    const items = [];
    for (let i = 0; i < medicineIds.length; i++) {
        const medId = medicineIds[i].value.trim();
        const qty   = quantities[i].value.trim();
        if (medId && qty && qty > 0) {
            items.push({ medicineId: Number(medId), quantity: Number(qty) });
        }
    }

    const orderData = {
        userId: Number(userId),
        orderType: orderType,
        items: items
    };

    try {
        const res = await fetch(`${API}/orders`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(orderData)
        });

        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to create order");
        }

        const created = await res.json();
        showSuccess("Order created successfully! Redirecting...");
        setTimeout(() => {
            window.location.href = `/order-detail.html?id=${created.id}`;
        }, 1500);

    } catch (err) {
        showError(err.message);
    }
}

// ── Helpers ──
function showSuccess(msg) {
    const el = document.getElementById("successAlert");
    el.textContent = msg;
    el.classList.add("show");
}

function showError(msg) {
    const el = document.getElementById("errorAlert");
    el.textContent = msg;
    el.classList.add("show");
    setTimeout(() => el.classList.remove("show"), 4000);
}