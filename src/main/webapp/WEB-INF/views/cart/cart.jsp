<%--
  Created by IntelliJ IDEA.
  User: cheng
  Date: 2026/4/19
  Time: 20:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Cart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/include/header.jsp"/>
    <div class="container">
        <h2>My Cart</h2>
        <div id="cartItems" class="panel"></div>
        <div class="cart-summary">
            <span><strong>Total: ¥<span id="totalPrice">0</span></strong></span>
            <button id="checkoutBtn" class="btn btn-solid">Checkout</button>
        </div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        async function loadCart() {
            const token = localStorage.getItem('token');
            const res = await fetch(ctx + '/api/cart', {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await res.json();
            if (data.code === 200) {
                renderCart(data.data.items, data.data.totalPrice);
            }
        }

        function renderCart(items, totalPrice) {
            const container = document.getElementById('cartItems');
            if (!items.length) {
                container.innerHTML = '<p>Your cart is empty.</p>';
                document.getElementById('totalPrice').innerText = '0';
                return;
            }
            container.innerHTML = items.map(item =>
                '<div class="cart-item">' +
                    '<span>' + item.productName + '</span>' +
                    '<span>¥' + item.price + '</span>' +
                    '<input type="number" value="' + item.quantity + '" min="1" data-pid="' + item.productId + '" class="qty-input">' +
                    '<button class="btn btn-secondary" onclick="updateCart(' + item.productId + ', this.previousElementSibling.value)">Update</button>' +
                    '<button class="btn btn-danger" onclick="removeFromCart(' + item.productId + ')">Remove</button>' +
                    '<span>Subtotal: ¥' + item.subtotal + '</span>' +
                '</div>'
            ).join('');
            document.getElementById('totalPrice').innerText = totalPrice;
        }

        window.updateCart = async (productId, quantity) => {
            const token = localStorage.getItem('token');
            await fetch(ctx + '/api/cart/update', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({ productId, quantity: parseInt(quantity) })
            });
            loadCart();
        };

        window.removeFromCart = async (productId) => {
            const token = localStorage.getItem('token');
            await fetch(ctx + '/api/cart/remove/' + productId, {
                method: 'DELETE',
                headers: { 'Authorization': 'Bearer ' + token }
            });
            loadCart();
        };

        document.getElementById('checkoutBtn').addEventListener('click', async () => {
            const token = localStorage.getItem('token');
            const address = prompt('Please enter your shipping address', 'Mock address: Campus Dorm');
            const res = await fetch(ctx + '/api/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({ address })
            });
            const data = await res.json();
            if (data.code === 200) {
                alert('Order created successfully. Please proceed to payment from Orders.');
                window.location.href = ctx + '/order/orders';
            } else {
                alert(data.message);
            }
        });

        loadCart();
    </script>
</body>
</html>
