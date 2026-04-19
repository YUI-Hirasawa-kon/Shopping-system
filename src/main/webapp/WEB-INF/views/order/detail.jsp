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
    <title>Order Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/include/header.jsp"/>
    <div class="container">
        <h2>Order Detail</h2>
        <div id="orderDetail"></div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        const orderId = new URLSearchParams(location.search).get('orderId');
        async function loadDetail() {
            const token = localStorage.getItem('token');
            const res = await fetch(ctx + '/api/orders/' + orderId, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await res.json();
            if (data.code === 200) {
                renderDetail(data.data);
            }
        }

        function renderDetail(order) {
            let logisticsHtml = '';
            if (order.logistics) {
                logisticsHtml = '<div class="logistics">' +
                        '<h3>Logistics</h3>' +
                        '<p>Carrier: ' + order.logistics.company + '</p>' +
                        '<p>Tracking No: ' + order.logistics.trackingNo + '</p>' +
                        '<p>Status: ' + order.logistics.status + '</p>' +
                        '<ul>' +
                            order.logistics.traces.map(t => '<li>' + t.time + ' ' + t.info + '</li>').join('') +
                        '</ul>' +
                    '</div>';
            }
            const html = '<div>' +
                    '<div class="panel">' +
                    '<p><strong>Order No:</strong> ' + order.orderNo + '</p>' +
                    '<p><strong>Total:</strong> ¥' + order.totalAmount + '</p>' +
                    '<p><strong>Status:</strong> ' + order.status + '</p>' +
                    '<p><strong>Address:</strong> ' + (order.address || 'Not provided') + '</p>' +
                    '<p><strong>Created At:</strong> ' + order.createTime + '</p>' +
                    '<p><strong>Paid At:</strong> ' + (order.payTime || 'Not paid') + '</p>' +
                    '<h3>Items</h3>' +
                    '<table class="table">' +
                        '<tr><th>Product</th><th>Price</th><th>Quantity</th></tr>' +
                        order.items.map(item =>
                            '<tr><td>' + item.productName + '</td><td>¥' + item.price + '</td><td>' + item.quantity + '</td></tr>'
                        ).join('') +
                    '</table>' +
                    logisticsHtml +
                    '</div>' +
                '</div>';
            document.getElementById('orderDetail').innerHTML = html;
        }
        loadDetail();
    </script>
</body>
</html>
