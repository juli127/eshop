**E-shop webapp based on servlets + JSP + Ajax:**
- There is possibility to connect to db using DataSource with Database Connection Pool or JDBC (choosing 'factoryClass' value in dbconfig.properties);
- Not authorized user can see, choose any product but cannot buy them, notification to register or login is shown (MainPageWithAllProducts.png);
- If user was registered before and try to register again, he/she will be resend to login page for login;
- Registration form has verification on input values;
- If user logged-in and he/she has empty cart, he/she will be resend to product page for shopping;
- If user logged-in and he/she has not empty cart, he/she will be resend to cart page for cart edition or making order;
- Cart can be edited (with Ajax): products can be added/removed (UserShoppingCart.png):
    - If some product's count becomes equals to zero, this product disappears from the cart;
    - If all products are removed from the cart, table with cart products list disappears at all;
- Press on 'make order' button creates the new order on the base of user's cart (UserOrder.png). Shopping cart becomes empty;
- Access to staff-only info (/admin) is made through the filter. If user has no access, forbidden info is shown (/resources/screenshots/ForbiddenResources.png);


  