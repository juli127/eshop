<%@ include file="includes/header.jsp" %>

<c:if test="${order != null}">
<br><div id="info">Thank you for your purchase! Your last order # ${order.orderNumber} includes:</div>
<br><br>

        <table id="cart" border=1>
            <div id="tableTitle">
                <tr>
                    <td>Product's name</td>
                    <td>Price</td>
                    <td>Quantity</td>
                </tr>
            </div>

            <c:forEach var="item" items="${order.products}">
                <tr>
                    <td><c:out value="${item.key.name}"/></td>
                    <td><c:out value="${item.key.price}"/> UAH</td>
                    <td><c:out value="${item.value}"/></td>
                </tr>
            </c:forEach>
        </table>

    <br><br>
        <div id="info">
            Total order's sum:  ${order.totalSum==null?0:order.totalSum} UAH
            <br>Total items' quantity: ${(order.itemsCount==null) ? 0 : order.itemsCount}
        </div>
 </c:if>

<%@ include file="includes/footer.jsp" %>
