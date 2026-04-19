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
    <title>Register - Shopping System</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <div class="auth-wrap">
        <div class="auth-card">
            <h2>Register</h2>
            <form id="registerForm">
                <input type="text" id="username" placeholder="Username" required>
                <input type="password" id="password" placeholder="Password" required>
                <input type="email" id="email" placeholder="Email (optional)">
                <button class="btn btn-solid" type="submit">Create Account</button>
            </form>
            <p class="helper-text">Already have an account? <a href="${pageContext.request.contextPath}/auth/login">Sign in</a></p>
            <div id="msg" class="error"></div>
        </div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        document.getElementById('registerForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const email = document.getElementById('email').value;
            const res = await fetch(ctx + '/api/auth/register', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({username, password, email})
            });
            const data = await res.json();
            if (data.code === 200) {
                alert('Registration successful. Please sign in.');
                window.location.href = ctx + '/auth/login';
            } else {
                document.getElementById('msg').innerText = data.message;
            }
        });
    </script>
</body>
</html>
