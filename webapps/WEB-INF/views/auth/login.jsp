<%--
  Created by IntelliJ IDEA.
  User: cheng
  Date: 2026/4/19
  Time: 20:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Shopping System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <div class="auth-wrap">
        <div class="auth-card">
            <h2>Login</h2>
            <form id="loginForm">
                <input type="text" id="username" placeholder="Username" required>
                <input type="password" id="password" placeholder="Password" required>
                <button class="btn btn-solid" type="submit">Sign In</button>
            </form>
            <p class="helper-text">No account yet? <a href="${pageContext.request.contextPath}/auth/register">Create one</a></p>
            <div id="msg" class="error"></div>
        </div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const res = await fetch(ctx + '/api/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password})
            });
            const data = await res.json();
            if (data.code === 200) {
                localStorage.setItem('token', data.data.token);
                localStorage.setItem('username', data.data.username);
                localStorage.setItem('role', data.data.role);
                window.location.href = ctx + '/product/list';
            } else {
                document.getElementById('msg').innerText = data.message;
            }
        });
    </script>
</body>
</html>
