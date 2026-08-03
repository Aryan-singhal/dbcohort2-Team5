// TICKET-ADV104 / ADV106 / ADV107 — EventSource live feed with prepend + slide-in animation.
(function () {

   const FEED_EL = document.getElementById("trade-feed"); if (!FEED_EL) return;

    const STREAM_URL = "/api/v1/trades/stream";

    let sse = null;
    let connectionStatus = "connecting";

    function updateConnectionBadge(text, variant) {
        const badge = document.getElementById("sse-status");
        if (!badge) return;

        badge.textContent = text;
        badge.className = variant;
    }

    function prependTradeRow(trade) {

    let statusModifier = "";

    if (trade.status === "MATCHED") {
        statusModifier = "trade-card--matched";
    } else if (trade.status === "UNMATCHED") {
        statusModifier = "trade-card--break";
    }

    const row = document.createElement("article");

    row.className =
        "trade-card " +
        statusModifier +
        " trade-card--new";

    row.innerHTML = `
        <header class="trade-card__header">
            <strong>${escapeHtml(trade.tradeRef)}</strong>
            <span>${escapeHtml(trade.status)}</span>
        </header>

        <div class="trade-card__body">
            <span>${escapeHtml(trade.symbol)}</span>
            <span>Qty: ${formatQty(trade.qty)}</span>
            <span>Price: ${formatPrice(trade.price)}</span>
            <span>${escapeHtml(trade.currency ?? "")}</span>
        </div>
    `;

    FEED_EL.prepend(row);

    setTimeout(() => {
        row.classList.remove("trade-card--new");
    }, 500);

    while (FEED_EL.children.length > 50) {
        FEED_EL.lastElementChild.remove();
    }
}

    function connect() {

        sse = new EventSource(STREAM_URL);

        sse.onopen = () => {
            connectionStatus = "live";
            updateConnectionBadge("Live", "live");
        };

        sse.onmessage = (event) => {
            try {
                const trade = JSON.parse(event.data);

                // ADV105 will replace this with prependTradeRow(trade)
               prependTradeRow(trade);
            } catch (err) {
                console.error("Invalid SSE payload", err);
            }
        };

        sse.onerror = () => {
            connectionStatus = "reconnecting";
            updateConnectionBadge("Reconnecting…", "reconnecting");

            // Do NOT reconnect manually.
            // EventSource handles reconnection automatically.
        };
    }

    window.addEventListener("beforeunload", () => {
        sse?.close();
    });

    function escapeHtml(str) {
    return String(str)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

function formatQty(value) {
    return new Intl.NumberFormat("en-US").format(value);
}

function formatPrice(value) {
    return new Intl.NumberFormat("en-US", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 4
    }).format(value);
}

    connect();

})();