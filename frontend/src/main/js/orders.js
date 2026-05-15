const API = "http://localhost:8080/api";

let allOrders = [];

// ── Fetch all orders on page load ──
async function loadOrders() {
    try {
        const res = await fetch(`${API}/orders`);
        if (!res.ok) throw new Error("Failed to fetch orders");
        allOrders = await res.json();
        renderOrders(allOrders);
    } catch (err) {
        showError(err.message);
        document.getElementById("loadingMsg").style.display = "none";
    }
}

// ── Render orders to table ──
function renderOrders(orders) {
    document.getElementById("loadingMsg").style.display = "none";

    const tbody = document.getElementById("ordersTableBody");
    const tableWrap = document.getElementById("tableWrap");
    const emptyMsg = document.getElementById("emptyMsg");

    document.getElementById("orderCount").textContent = `${allOrders.length} total orders`;

    if (orders.length === 0) {
        tableWrap.style.display = "none";
        emptyMsg.style.display = "block";
        return;
    }

    emptyMsg.style.display = "none";
    tableWrap.style.display = "block";

    tbody.innerHTML = orders.map(order => `
        <tr>
            <td class="order-id">#${order.id}</td>
            <td><span class="badge badge-${order.orderType}">${order.orderType}</span></td>
            <td>${order.userId}</td>
            <td style="color:#fff;font-weight:600">LKR ${order.totalAmount.toFixed(2)}</td>
            <td><span class="badge badge-${order.status}">${order.status}</span></td>
            <td style="color:#64748b">${formatDate(order.orderDate)}</td>
            <td>
                <div class="actions">
                    <button class="btn btn-sm btn-view" onclick="viewOrder(${order.id})">View</button>
                    ${order.status === 'PENDING' ? `
                        <button class="btn btn-sm btn-cancel" onclick="cancelOrder(${order.id})">Cancel</button>
                    ` : ''}
                    <button class="btn btn-sm btn-delete" onclick="deleteOrder(${order.id})">Delete</button>
                </div>
            </td>
        </tr>
    `).join('');
}

// ── Filter orders ──
function filterOrders() {
    const userId   = document.getElementById("searchUserId").value.trim();
    const status   = document.getElementById("filterStatus").value;
    const type     = document.getElementById("filterType").value;

    const filtered = allOrders.filter(o => {
        const matchUser   = !userId || String(o.userId).includes(userId);
        const matchStatus = !status || o.status === status;
        const matchType   = !type   || o.orderType === type;
        return matchUser && matchStatus && matchType;
    });

    renderOrders(filtered);
}

// ── Navigate to order detail ──
function viewOrder(id) {
    window.location.href = `/order-detail.html?id=${id}`;
}

// ── Cancel order ──
async function cancelOrder(id) {
    if (!confirm("Cancel this order?")) return;
    try {
        const res = await fetch(`${API}/orders/${id}/cancel`, { method: "PUT" });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to cancel order");
        }
        showSuccess("Order cancelled successfully!");
        loadOrders();
    } catch (err) {
        showError(err.message);
    }
}

// ── Delete order ──
async function deleteOrder(id) {
    if (!confirm("Delete this order permanently?")) return;
    try {
        const res = await fetch(`${API}/orders/${id}`, { method: "DELETE" });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Failed to delete order");
        }
        showSuccess("Order deleted successfully!");
        loadOrders();
    } catch (err) {
        showError(err.message);
    }
}

// ── Helpers ──
function formatDate(dateStr) {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleDateString("en-US", {
        year: "numeric", month: "short", day: "numeric"
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
loadOrders();