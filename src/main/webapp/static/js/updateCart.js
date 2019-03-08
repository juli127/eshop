// products.jsp: just change quantity of selected item on the page, don't deal with DB
function plus(productId) {
    var elem = document.getElementById('pq' + productId);
    elem.innerHTML = +elem.innerHTML + 1;
}

// products.jsp: just change quantity of selected item on the page, don't deal with DB
function minus(productId) {
    var elem = document.getElementById('pq' + productId);
    if (elem.innerHTML > 1) {
        elem.innerHTML = +elem.innerHTML - 1;
    }
}

// products.jsp: add some items' quantity to the cart -> save these items to cart into DB and get updated cart back through JSON
function buy(userId, productId) {
    if (userId == null || userId == "") {
        alert("You should register or login before shopping!");
    } else {
        var elem = document.getElementById('pq' + productId);
        var qnt = +elem.innerHTML;
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {
                'action': 'add',
                'productId': productId,
                'quantity': qnt
            },
            dataType: 'json',
            success: function (response) {
                parseRespose(response);
            },
            error: function (e) {
                console.log(e.message);
            }
        });
        alert("This item was added to your cart");
    }
}

// cart.jsp: delete 1 item from cart -> delete item from cart from DB and get updated cart back through JSON
function deleteFromCart(productId) {
    var elem = document.getElementById('q' + productId);
    var qnt = +elem.innerHTML;
    if (elem.innerHTML > 0) {
        elem.innerHTML = qnt - 1;
        $.ajax({
            type: "POST",
            url: "./cart",
            data: {
                'action': 'remove',
                'productId': productId,
                'quantity': 1
            },
            dataType: 'json',
            success: function (response) {
                parseRespose(response);
            },
            error: function (e) {
                console.log(e.message);
            }
        });
    }
}

// cart.jsp: add 1 item to cart -> save item to cart into DB and get updated cart back through JSON
function addToCart(productId) {
    var elem = document.getElementById('q' + productId);
    elem.innerHTML = +elem.innerHTML + 1;
    $.ajax({
        type: "POST",
        url: "./cart",
        data: {
            'action': 'add',
            'productId': productId,
            'quantity': 1
        },
        dataType: 'json',
        success: function (response) {
            parseRespose(response);
        },
        error: function (e) {
            console.log(e.message);
        }
    });
}

// JSON parser: got JSON string with updated Cart from '/cart' servlet, parse it and update some fields on 'cart.jsp' page
function parseRespose(responce) {

    // get updated cartSize and totalSum from JSON:
    document.getElementById('goodsCountField').innerHTML = responce.cartSize;
    document.getElementById('totalSumField').innerHTML = responce.totalSum;

    // got from JSON updated cart info -------------------
    var cartJSONmap = responce.products;
    var recordsCount = Object.keys(cartJSONmap).length;
    var newTable = "";

    // repaint updated cart on cart.jsp
    // repaint all table to avoid the situation when the row with 0 items is shown
    if (recordsCount > 0) {
        newTable = "<table id=\"cart\" border=1>" +
            "<div id='tableTitle'><tr><td>Product's name</td><td>Price</td><td>Quantity</td></tr></div>" +
            "<c:forEach var = 'i' begin = '0' end = '" + (recordsCount - 1) + "'>" +
            "<tr>";

        for (var i = 0; i < recordsCount; i++) {
            var product = Object.keys(cartJSONmap)[i];
            var qnt = Object.values(cartJSONmap)[i];
            var pId = JSON.parse(product).productId;

            newTable = newTable + "<td><div id=\"productName\">" + JSON.parse(product).name + "</div></td>" +
                "<td>" + JSON.parse(product).price + " UAH</td>" +
                "<td><button onclick=\"deleteFromCart('" + pId + "')\">-</button>" +
                "<span id='q" + pId + "'>" + qnt + "</span>" +
                "<button onclick=\"addToCart('" + pId + "')\">+</button></td></tr>";
        }
        newTable = newTable + "</c:forEach></table>";
    }
    else {
        // no items in cart, so, cart won't be shown (newTable is "") and clear field "summary" on cart.jsp
        document.getElementById('summary_info').innerHTML = "";
    }
    document.getElementById('cart_content').innerHTML = newTable;
}

// make order means that products from 'Cart' object will be moved to 'Order' object. Cart becomes empty.
function makeOrder(userId) {
    var qnt = document.getElementById('q' + userId).innerHTML;
    $.ajax({
        type: "POST",
        url: "./order",
        data: {
            'action': 'makeOrder',
            'userID': userId
        },
        dataType: 'json',
        success: function (response) {
            parseRespose(response);
        },
        error: function (e) {
            console.log(e.message);
        }
    });
}
