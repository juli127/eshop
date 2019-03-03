<%@ page contentType="text/html; charset=UTF-8" %>
</div><%--for id="content" --%>
</div><%--for id="page" --%>
<%--</p>--%>

<br><br>
<div id="footer" class="footer">
    <p>Copyright (c) by Kramarenko Iuliia</p>
</div>

</div> <%--for id="wrapper"--%>
</body>
</html>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<%--<script src="./js/updateCart.js"></script>--%>
<%--<script src="./../../js/updateCart.js"></script>--%>

<script>
    // scripts for 'product' page
    function plus(productId) {
        alert('plus: productId == null ? ' + (productId == null));
        alert('plus: from form == ' + (document.getElementById('productId').innerText));
        var elem = document.getElementById('productQnt' + productId);
        var qnt = +elem.innerHTML + 1;
        // alert('plus: qnt = ' + qnt);
        elem.innerHTML = qnt;
    }

    function minus(productId) {
        alert('minus: productId == null ? ' + (productId == null));
        var elem = document.getElementById('productQnt' + productId);
        var qnt = +elem.innerHTML;
        if (elem.innerHTML > 0) {
            elem.innerHTML = qnt - 1;
        }
        // alert('minus: qnt = ' + qnt);
    }

    function buy(productId) {
        var elem = document.getElementById('productQnt' + productId);
        var qnt = +elem.innerHTML;
        var userid = ${sessionScope.user.id == null? 0: sessionScope.user.id};
        alert('user.id=' + userid);
        if (userid == null || userid.equals("")) {
            alert("Login or register, please, to buy something!");
        } else {
            alert("Покупаем товар!");
            $.ajax({
                type: "POST",
                url: "./cart",
                data: "addPurchase=" + productId + ":" + qnt,
                success: function (response) {
                    parseRespose(response);
                }
            });
            alert(${sessionScope.user.name} +", this item was added to your cart");
        }
    }

    // scripts for 'cart' page
    function deleteFromCart(productId) {
        var elem = document.getElementById('q' + productId);
        var qnt = +elem.innerHTML;
        if (elem.innerHTML > 0) {
            elem.innerHTML = qnt - 1;
            $.ajax({
                type: "POST",
                url: "./cart",
                data: "removePurchase=" + productId + ":" + 1,
                // data: { removePurchase : productId + ":" + qnt },
                success: function (response) {
                    parseRespose(response);
                }
            });
        }
    }

    function addToCart(productId) {
        var elem = document.getElementById('q' + productId);
        var qnt = +elem.innerHTML + 1;
        elem.innerHTML = qnt;
        $.ajax({
            type: "POST",
            url: "./cart",
            data: "addPurchase=" + productId + ":" + 1,
            success: function (response) {
                parseRespose(response);
            }
        });
    }

    function parseRespose(response) {
        var respData = response.toString().split("<br>");
        for (var i = 0; i < respData.length; i++) {
            if (respData[i].startsWith("header:")) {
                var start = respData[i].indexOf("header: ");
                if (start >= 0) {
                    respData[i] = respData[i].substring(start + 8).trim();
                    if (respData[i].startsWith("totalSum:")) {
                        document.getElementById('TotalSum').innerHTML = respData[i].substring(start + 9).trim();
                    }
                    if (respData[i].startsWith("cartSize:")) {
                        document.getElementById('goodsCount').innerHTML = respData[i].substring(start + 9).trim();
                    }
                }
            }
        }
    }

    function makeOrder(userId) {
        var qnt = document.getElementById('q' + userId).innerHTML;
        $.ajax({
            type: "POST",
            url: "./order",
            data: "orderUserID=" + (myId),
            success: function (response) {
                alert('Заказ оформлен');
            }
        });
    }
</script>