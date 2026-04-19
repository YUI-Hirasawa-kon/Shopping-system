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
    <title>Products</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/views/include/header.jsp"/>
    <div class="container">
        <h2>Products</h2>
        <div class="toolbar panel">
            <div class="search-group">
                <input type="text" id="keyword" placeholder="Search products">
                <button id="searchBtn" class="btn btn-solid">Search</button>
            </div>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/cart/show">Go to Cart</a>
        </div>
        <div id="productGrid" class="product-grid"></div>
        <div class="pagination" id="pagination"></div>
    </div>
    <script>
        const ctx = "${pageContext.request.contextPath}";
        let currentPage = 1;
        let currentKeyword = '';

        async function loadProducts(page, keyword) {
            const token = localStorage.getItem('token');
            if (!token) { window.location.href = ctx + '/auth/login'; return; }
            let url = ctx + '/api/products?page=' + page + '&size=8';
            if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
            const res = await fetch(url, {
                headers: { 'Authorization': 'Bearer ' + token }
            });
            const data = await res.json();
            if (data.code === 200) {
                renderProducts(data.data.list);
                renderPagination(data.data.total, page, 8);
            } else if (data.code === 401) {
                localStorage.clear();
                window.location.href = ctx + '/auth/login';
            }
        }

        function renderProducts(products) {
            const grid = document.getElementById('productGrid');
            if (!products.length) {
                grid.innerHTML = '<p>No products found.</p>';
                return;
            }
            grid.innerHTML = products.map(p =>
                '<div class="product-card">' +
                    '<img src="' + (p.imageUrl || 'https://via.placeholder.com/150') + '" alt="' + p.name + '">' +
                    '<h3>' + p.name + '</h3>' +
                    '<p class="price">¥' + p.price + '</p>' +
                    '<p class="muted">Stock: ' + p.stock + '</p>' +
                    '<button class="btn btn-solid" onclick="addToCart(' + p.id + ')">Add to Cart</button>' +
                '</div>'
            ).join('');
        }

        function renderPagination(total, page, size) {
            const totalPages = Math.ceil(total / size);
            const paginationDiv = document.getElementById('pagination');
            let html = '';
            for (let i = 1; i <= totalPages; i++) {
                html += '<button class="' + (i === page ? 'active' : '') +
                    '" onclick="loadProducts(' + i + ', \'' + currentKeyword + '\')">' + i + '</button>';
            }
            paginationDiv.innerHTML = html;
        }

        async function addToCart(productId) {
            const token = localStorage.getItem('token');
            const res = await fetch(ctx + '/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({ productId, quantity: 1 })
            });
            const data = await res.json();
            if (data.code === 200) {
                alert('Added to cart.');
            } else {
                alert(data.message);
            }
        }

        document.getElementById('searchBtn').addEventListener('click', () => {
            currentKeyword = document.getElementById('keyword').value;
            currentPage = 1;
            loadProducts(currentPage, currentKeyword);
        });

        loadProducts(1, '');
    </script>
</body>
</html>
