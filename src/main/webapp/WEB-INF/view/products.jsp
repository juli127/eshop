<%@ include file="includes/header.jsp" %>

<c:if test="${sessionScope.products.size() > 0}">
    <c:forEach var="product" items="${sessionScope.products}">
        <div class="productsTable">
            <table border="1">
                <td>
                    <img src="static/images/${product.image}" alt="" width="230" height="300">
                    <div class="productName" id="productName"><c:out value="${product.name}"/></div>
                    <div id="price"><c:out value="${product.price}"/> UAH</div>
                    <div class="productDescription">
                        <input type="hidden" id="productId" value="${product.id}"/>
                        <input type='button' onclick="minus('${product.id}')" value='-' />
                        <span id='pq${product.id}'>1</span>
                        <input type='button' onclick="plus('${product.id}')" value='+' />
                        <input type='button' onclick="buy('${sessionScope.user.id}', '${product.id}')" value='Buy'/>
                    </div>
                </td>
            </table>
        </div>
    </c:forEach>
</c:if>

<%@ include file="includes/footer.jsp" %>

