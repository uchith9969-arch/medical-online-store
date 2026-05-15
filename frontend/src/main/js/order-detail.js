const API = "http://localhost:8080/api";

// Get order ID from URL
const urlParams = new URLSearchParams(window.location.search);
const orderId = urlParams.get("id");

let currentOrder = null;

// ── Load order and items ──
async function loadOrder() {
    if (!orderId) { showError("No order ID provided"); return; }

    try {
        const [orderRes, itemsRes] = await Promise.all([
            fetch(`${API}/orders/${orderId}`),
            fetch(`${API}/orders/${orderId}/items`)
        ]);

        if (!orderRes.ok) throw new Error("Order not found");

        currentOrder = await orderRes.json();
        const items  = await itemsRes.json();

        renderOrder(currentOrder);
        renderItems(items);

        document.getElementById("loadingMsg").style.display = "none";
        document.getElementById("orderCard").style.display  = "block";
        document.getElementById("itemsCard").style.display  = "block";

    } catch (err) {
        document.getElementById("loadingMsg").style.display = "none";
        showError(err.message);
    }
}

// ── Render order info ──
function renderOrder(order) {
    document.title = `Order #${order.id} - MediCore`;

    document.getElementById("orderId").textContent    = `#${order.id}`;
    document.getElementById("orderDate").textContent  = `Placed on ${formatDate(order.orderDate)}`;
    document.getElementById("userId").textContent     = `#${order.userId}`;
    document.getElementById("totalAmount").textContent = `LKR ${order.totalAmount.toFixed(2)}`;
    document.getElementById("priority").textContent   = order.calculatePriority ?? (order.orderType === "URGENT" ? "10 (High)" : "1 (Normal)");
    document.getElementById("orderSummary").textContent = `📋 ${order.entitySummary || order.orderType + ' Order #' + order.id + ' | User: ' + order.userId + ' | Total: LKR ' + order.totalAmount + ' | Status: ' + order.status}`;

    // Type badge
    const typeBadge = document.getElementById("orderTypeBadge");
    typeBadge.textContent = order.orderType;
    typeBadge.className   = `badge badge-${order.orderType}`;

    // Status badge
    const statusBadge = document.getElementById("statusBadge");
    statusBadge.textContent = order.status;
    statusBadge.className   = `badge badge-${order.status}`;

    // Set status select
    document.getElementById("statusSelect").value = order.status;

    // Hide status update if cancelled or delivered
    if (order.status === "CANCELLED" || order.status === "DELIVERED") {
        document.getElementById("statusUpdateSection").style.display = "none";
    }
}

// ── Render order items ──
function renderItems(items) {
    const container = document.getElementById("itemsContent");

    if (items.length === 0) {
        container.innerHTML = `<div class="empty">No items in this order.</div>`;
        return;
    }

    container.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>Item ID</th>
                    <th>Medicine ID</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Total</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                ${items.map(item => `
                    <tr>
                        <td style="color:#64748b">#${item.id}</td>
                        <td>#${item.medicineId}</td>
                        <td>LKR ${item.unitPrice.toFixed(2)}</td>
                        <td>
                            ${currentOrder.status === "PENDING" ? `
                                <div style="display:flex;align-items:center;gap:8px">
                                    <input type="number" id="qty-${item.id}" value="${item.quantity}" min="1"
                                        style="width:70px;background:rgba(255,255,255,0.05);border:1px solid rgba(255,255,255,0.1);border-radius:6px;padding:6px 10px;color:#fff;font-size:14px;outline:none" />
                                    <button class="btn btn-sm btn-view" onclick="updateQuantity(${item.id})">Save</button>
                                </div>
                            ` : item.quantity}
                        </td>
                        <td style="color:#00c896;font-weight:600">LKR ${item.totalPrice.toFixed(2)}</td>
                        <td>
                            ${currentOrder.status === "PENDING" ? `
                                <button class="btn btn-sm btn-delete" onclick="removeItem(${item.id})">Remove</button>
                            ` : '—'}
                        </td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;
}

// ── Update order status ──
async function updateStatus() {
    const status = document.getElementById("statusSelect").value;
    try {
        const res = await fetch(`${API}/orders/${orderId}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status })
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to update status");
        }
        showSuccess("Status updated successfully!");
        loadOrder();
    } catch (err) {
        showError(err.message);
    }
}

// ── Update item quantity ──
async function updateQuantity(itemId) {
    const qty = document.getElementById(`qty-${itemId}`).value;
    try {
        const res = await fetch(`${API}/orders/items/${itemId}/quantity`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ quantity: Number(qty) })
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to update quantity");
        }
        showSuccess("Quantity updated!");
        loadOrder();
    } catch (err) {
        showError(err.message);
    }
}

// ── Remove item ──
async function removeItem(itemId) {
    if (!confirm("Remove this item from the order?")) return;
    try {
        const res = await fetch(`${API}/orders/items/${itemId}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to remove item");
        }
        showSuccess("Item removed!");
        loadOrder();
    } catch (err) {
        showError(err.message);
    }
}

// ── Helpers ──
function formatDate(dateStr) {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleDateString("en-US", {
        weekday: "long", year: "numeric", month: "long", day: "numeric"
    });
}

function showSuccess(msg) {
    const el = document.getElementById("successAlert");
    el.textContent = msg;
    el.classList.add("show");
    setTimeout(() => el.classList.remove("show"), 4000);
}

function showError(msg) {
    const el = document.getElementById("errorAlert");
    el.textContent = msg;
    el.classList.add("show");
    setTimeout(() => el.classList.remove("show"), 4000);
}

// ── Init ──
loadOrder();