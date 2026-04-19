<%--
  Created by IntelliJ IDEA.
  User: cheng
  Date: 2026/4/19
  Time: 20:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="navbar">
    <div class="nav-brand">Shopping System</div>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/product/list">Products</a>
        <a href="${pageContext.request.contextPath}/cart/show">Cart</a>
        <a href="${pageContext.request.contextPath}/order/orders">Orders</a>
        <span id="user-info" class="muted"></span>
        <button id="logoutBtn" class="btn btn-danger" style="display:none;">Logout</button>
        <button id="darkModeToggle" class="btn btn-secondary">Dark Mode</button>
    </div>
</div>
<script>
    // Show current user when logged in
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    if (token && username) {
        document.getElementById('user-info').innerHTML = 'Hi, ' + username;
        document.getElementById('logoutBtn').style.display = 'inline-block';
    }
    // Logout
    document.getElementById('logoutBtn')?.addEventListener('click', () => {
        localStorage.clear();
        window.location.href = '${pageContext.request.contextPath}/auth/login';
    });
    // Dark mode toggle
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (localStorage.getItem('darkMode') === 'true') {
        document.body.classList.add('dark-mode');
    }
    darkModeToggle.addEventListener('click', () => {
        document.body.classList.toggle('dark-mode');
        localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
    });
</script>
