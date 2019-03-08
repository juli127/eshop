<%@ include file="includes/header.jsp" %>

<br><br>
<h3>${message}</h3>
<br><br>

    <c:if test="${cartSize > 0}">
        <div id="cart_content">
        <table id="cart" border=1>
            <div id="tableTitle">
                <tr>
                    <td>Product's name</td>
                    <td>Price</td>
                    <td>Quantity</td>
                </tr>
            </div>

            <c:forEach var="purchase" items="${productsInCart}">
                <tr>
                    <td>
                        <div id="productName"><c:out value="${purchase.key.name}"/></div>
                    </td>
                    <td>
                        <div id='price${purchase.key.id}'><c:out value="${purchase.key.price}"/> UAH</div>
                    </td>
                    <td>
                        <button onclick="deleteFromCart('${purchase.key.id}')">-</button>
                        <span id='q${purchase.key.id}'>${purchase.value}</span>
                        <button onclick="addToCart('${purchase.key.id}')">+</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
        </div>

        <br><br>
        <div id="summary_info">
        <h3>Total cart's sum: <span id="totalSumField">${totalSum==null?0:totalSum}</span> UAH</h3>
        <br><br>
        <button onclick="makeOrder('${userId}')"><font size="3" style="shape-rendering: crispEdges">Make order</font></button>
        </div>

    </c:if>


<%@ include file="includes/footer.jsp" %>
