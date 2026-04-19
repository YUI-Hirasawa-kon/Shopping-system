<%--
  Created by IntelliJ IDEA.
  User: cheng
  Date: 2026/4/19
  Time: 20:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Orders</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/include/header.jsp"/>
    <div class="container">
        <h2>Order List</h2>
        <div id="orderList"></div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        async function loadOrders() {
            const token = localStorage.getItem('token');
            const res = await fetch(ctx + '/api/orders', {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await res.json();
            if (data.code === 200) {
                renderOrders(data.data);
            }
        }

        function renderOrders(orders) {
            const container = document.getElementById('orderList');
            if (!orders.length) {
                container.innerHTML = '<div class="panel"><p>No orders yet.</p></div>';
                return;
            }
            container.innerHTML = orders.map(order => {
                const payBtn = order.status === 'PENDING_PAYMENT'
                    ? '<button class="btn btn-solid" onclick="payOrder(' + order.orderId + ')">Mock Pay</button>'
                    : '';
                return '<div class="order-card">' +
                    '<div>Order No: ' + order.orderNo + '</div>' +
                    '<div>Total: ¥' + order.totalAmount + '</div>' +
                    '<div>Status: ' + order.status + '</div>' +
                    '<div>Created At: ' + order.createTime + '</div>' +
                    '<div class="order-actions">' +
                        '<button class="btn btn-secondary" onclick="viewDetail(' + order.orderId + ')">View Detail</button>' +
                        payBtn +
                    '</div>' +
                '</div>';
            }).join('');
        }

        window.viewDetail = (orderId) => {
            window.location.href = ctx + '/order/detail?orderId=' + orderId;
        };

        window.payOrder = async (orderId) => {
            const token = localStorage.getItem('token');
            const res = await fetch(ctx + '/api/orders/' + orderId + '/pay', {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await res.json();
            if (data.code === 200) {
                alert('Payment successful');
                loadOrders();
            } else {
                alert(data.message);
            }
        };

        loadOrders();
    </script>
</body>
</html>
