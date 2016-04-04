package rc.diego.controller;

import rc.diego.model.VO.VOCd;
import rc.diego.model.VO.VOShoppingCart;
import rc.diego.model.VO.VOUser;
import rc.diego.model.persistence.Connector.MySQLContract;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by entakitos on 19/02/16.
 */
public class Controller extends CustomHttpServlet {

    private final String PARAMETER_ACTION = "action";
    private final String PARAMETER_PRODUCT = "product";
    private final String PARAMETER_QUANTITY = "quantity";
    private final String PARAMETER_ERROR = "error";

    private final String ACTION_SHOW_INDEX = "index";
    private final String ACTION_SHOW_PRODUCT_INFO = "productInfo";
    private final String ACTION_SHOW_SHOPPING_CART = "shoppingCart";
    private final String ACTION_SHOW_SIGN_IN = "signIn";
    private final String ACTION_SHOW_SIGN_UP = "signUp";
    private final String ACTION_BUY_ITEM = "buyItem";
    private final String ACTION_ERASE_ITEM = "eraseItem";
    private final String ACTION_UPDATE_ITEM = "updateItem";
    private final String ACTION_CHECKOUT = "checkout";
    private final String ACTION_SIGN_IN = "signInUser";
    private final String ACTION_SIGN_UP = "signUpUser";
    private final String ACTION_CONFIRM_PAYMENT = "confirmPayment";
    private final String ACTION_RESET = "reset";

    private final String ADMIN_ACTION_SHOW_STOCK = "admin/products/show";
    private final String ADMIN_ACTION_EDIT_ITEM = "admin/products/edit";
    private final String ADMIN_ACTION_DELETE_ITEM = "admin/products/delete";
    private final String ADMIN_ACTION_SAVE_ITEM = "admin/products/save";

    private final String ADMIN_ACTION_SHOW_USERS = "admin/users/show";
    private final String ADMIN_ACTION_EDIT_USER = "admin/users/edit";
    private final String ADMIN_ACTION_ACTIVATE_USER = "admin/users/activate";
    private final String ADMIN_ACTION_DEACTIVATE_USER = "admin/users/deactivate";
    private final String ADMIN_ACTION_SAVE_USER = "admin/users/save";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        VOShoppingCart shoppingCart;
        VOUser user;
        VOCd cd;
        ArrayList<VOUser> users;
        int id;

        HttpSession session=req.getSession(false);

        if(session == null) { //crease unha sesion si non existe
            session = req.getSession(true);
            getTaskMapper().initializeSession(session);
        }

        try {
            switch ((String) req.getParameter(PARAMETER_ACTION)) {
                case ACTION_SHOW_INDEX:
                    shoppingCart=getTaskMapper().getAllCds();
                    req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);
                    getViewManager().showIndex();
                    break;
                case ACTION_SHOW_PRODUCT_INFO:

                    cd=new VOCd();
                    cd.setId(Integer.parseInt(req.getParameter(PARAMETER_PRODUCT)));

                    if (getTaskMapper().getCd(cd)) {

                        shoppingCart = new VOShoppingCart();

                        getTaskMapper().addToShoppingCart(
                                shoppingCart,
                                cd
                        );

                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);
                        getViewManager().showProductInfo();
                    }else{
                        shoppingCart=getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

                        getViewManager().showIndex();
                    }

                    break;
                case ACTION_SHOW_SHOPPING_CART:
                    getViewManager().showShoppingCart();
                    break;
                case ACTION_SHOW_SIGN_IN:
                    getViewManager().showSignIn();
                    break;
                case ACTION_SHOW_SIGN_UP:
                    getViewManager().showSignUp();
                    break;
                case ACTION_CHECKOUT:
                    getViewManager().showPaymentData();
                    break;
                case ACTION_CONFIRM_PAYMENT:

                    user = user=((VOUser) session.getAttribute(VOUser.SESSION_ATTRIBUTE_USER));

                    if(user.getFirstName() != null && user.getFirstName().length() > 0) {
                        if(getTaskMapper().insertOrder(
                                (VOUser) session.getAttribute(VOUser.SESSION_ATTRIBUTE_USER),
                                (VOShoppingCart) session.getAttribute(VOShoppingCart.SESSION_ATTRIBUTE_SHOPPING_CART)
                        )) {

                            getTaskMapper().sendConfirmPaymentMail(
                                    (VOUser) session.getAttribute(VOUser.SESSION_ATTRIBUTE_USER),
                                    (VOShoppingCart) session.getAttribute(VOShoppingCart.SESSION_ATTRIBUTE_SHOPPING_CART)
                            );

                            getViewManager().showPayment();

                        }else{
                            req.setAttribute(PARAMETER_ERROR,"Se ha producido un error. No se dispone de suficiente sotck para algunos de los productos seleccionados");
                            getViewManager().showError();
                        }

                    }else{
                        getViewManager().showSignIn();
                    }

                    break;
                case ACTION_BUY_ITEM:
                    cd=new VOCd();
                    cd.setId(Integer.parseInt(req.getParameter(PARAMETER_PRODUCT)));

                    if (getTaskMapper().getCd(cd)) {

                        cd.setQuantity(Integer.parseInt(req.getParameter(PARAMETER_QUANTITY)));


                        shoppingCart = (VOShoppingCart) session.getAttribute(VOShoppingCart.SESSION_ATTRIBUTE_SHOPPING_CART);

                        getTaskMapper().addToShoppingCart(
                                shoppingCart,
                                cd
                        );

                        getViewManager().showShoppingCart();
                    }else{
                        shoppingCart=getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

                        getViewManager().showIndex();
                    }


                    break;
                case ACTION_ERASE_ITEM:

                    Enumeration enumeration=req.getParameterNames();

                    while (enumeration.hasMoreElements()) {
                        String element = (String)enumeration.nextElement();

                        if(element.contains("checkbox-")) {

                            VOCd VOCd =new VOCd();
                            VOCd.setId(Integer.parseInt(element.replace("checkbox-", "").trim()));

                            try {
                                getTaskMapper().removeFromShoppingCart(
                                        (VOShoppingCart) session.getAttribute(VOShoppingCart.SESSION_ATTRIBUTE_SHOPPING_CART),
                                        VOCd
                                );

                                /*
                                    Execútase dentro de un bloque try catch por si se dese a
                                    posibilidade de que se manipulara o html
                                    e se intentara borrar un elemento que non existe no carrito
                                 */
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                    getViewManager().showShoppingCart();
                    break;
                case ACTION_UPDATE_ITEM:
                    //TODO:FALTA REALIZAR ACCION DE ACTUALIZAR UN ITEM
                    getViewManager().showShoppingCart();
                    break;
                case ACTION_RESET:

                    session.setAttribute(VOUser.SESSION_ATTRIBUTE_USER,new VOUser());

                    getTaskMapper().initializeShoppingCart(
                            (VOShoppingCart) session.getAttribute(VOShoppingCart.SESSION_ATTRIBUTE_SHOPPING_CART)
                    );

                    shoppingCart=getTaskMapper().getAllCds();
                    req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

                    getViewManager().showIndex();

                    break;
                case ACTION_SIGN_IN:
                    user=((VOUser) session.getAttribute(VOUser.SESSION_ATTRIBUTE_USER));

                    getTaskMapper().setUserData(
                            req.getParameter(VOUser.PARAMETER_DNI),
                            null,
                            null,
                            null,
                            req.getParameter(VOUser.PARAMETER_PASSWORD),
                            user
                    );

                    if(getTaskMapper().signInUser(user)){  //usuario logueado correctamente
                        shoppingCart=getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

                        getViewManager().showIndex();
                    }else{
                        req.setAttribute(PARAMETER_ERROR,"Los datos de usuario no son correctos o el usuario no existe. Por favor, inténtelo de nuevo.");

                        getViewManager().showError();
                    }

                    break;
                case ACTION_SIGN_UP:


                    //TODO:mensaxe de error ao rexistrar o usuario
                    user=((VOUser) session.getAttribute(VOUser.SESSION_ATTRIBUTE_USER));

                    getTaskMapper().setUserData(
                            req.getParameter(VOUser.PARAMETER_DNI),
                            req.getParameter(VOUser.PARAMETER_FIRST_NAME),
                            req.getParameter(VOUser.PARAMETER_LAST_NAME),
                            req.getParameter(VOUser.PARAMETER_MAIL),
                            req.getParameter(VOUser.PARAMETER_PASSWORD),
                            user
                    );

                    //TODO: Intentar registraro ususario na base de datos
                    if(getTaskMapper().signUpUser(user)) {
                        //TODO: por ahora unha vez se registra un usuario, se mostraa páxina inicial
                        shoppingCart = getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS, shoppingCart);

                        getViewManager().showIndex();
                    }else{
                        req.setAttribute(PARAMETER_ERROR,"Se ha producido un error. No se puede registrar un usuario con ese DNI");

                        getViewManager().showError();
                    }

                    break;

                // Admin things
                case ADMIN_ACTION_EDIT_ITEM:
                    try {
                        id = Integer.parseInt(req.getParameter("item"));
                    } catch (NumberFormatException e){
                        id = 0;
                    }
                    cd = new VOCd();
                    cd.setId(id);

                    if (id<0){
                        req.setAttribute(PARAMETER_ERROR,"Se ha producido un error. No se puede encontrar un cd con ese identificador");
                        getViewManager().showError();
                    } else {
                        if (getTaskMapper().getCd(cd)){
                            System.err.println("-- Edit item " + id);
                            req.setAttribute(VOShoppingCart.SESSION_ITEM, cd);
                            getViewManager().showEditProduct();
                        } else {
                            System.err.println("-- Edit item " + 0);
                            cd.setId(0);
                            req.setAttribute(VOShoppingCart.SESSION_ITEM, cd);
                            getViewManager().showEditProduct();
                        }
                    }
                    break;

                case ADMIN_ACTION_SHOW_STOCK:
                    System.err.println("-- Show Stocks");
                    shoppingCart=getTaskMapper().getAllCds();
                    req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);
                    getViewManager().showStocks();
                    break;
                case ADMIN_ACTION_DELETE_ITEM:
                    VOCd cd3 = new VOCd();
                    try {
                        cd3.setId(Integer.parseInt(req.getParameter("item")));
                        System.err.println("-- Delete item "+ cd3.getId());

                        getTaskMapper().deleteCd(cd3);

                        shoppingCart=getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);
                        getViewManager().showStocks();

                    } catch (NumberFormatException e){
                        req.setAttribute(PARAMETER_ERROR,"Not a number");
                        getViewManager().showError();
                    }
                    break;
                case ADMIN_ACTION_SAVE_ITEM:
                    System.err.println("-- Save item");
                    VOCd cd2 = new VOCd();
                    try {
                        cd2.setId(Integer.parseInt(req.getParameter("id")));
                        cd2.setTitle(req.getParameter("name"));
                        cd2.setImage(req.getParameter("imagen"));
                        cd2.setAuthor(req.getParameter("author"));
                        cd2.setQuantity(Integer.parseInt(req.getParameter("quantity")));
                        cd2.setUnitaryPrice(Float.parseFloat(req.getParameter("price")));
                        cd2.setDescription(req.getParameter("description"));
                        cd2.setCountry(req.getParameter("country"));

                        System.out.println(cd2.toString());

                        if (cd2.getId()!=0) {
                            getTaskMapper().updateCd(cd2);
                        } else {
                            getTaskMapper().createCd(cd2);
                        }

                        shoppingCart=getTaskMapper().getAllCds();
                        req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);
                        getViewManager().showStocks();
                    } catch (NumberFormatException e) {
                        System.err.println("[ERR] Not a Number");
                        req.setAttribute(PARAMETER_ERROR,"Not a number");
                        getViewManager().showError();
                    }
                    break;


                case ADMIN_ACTION_SHOW_USERS:
                    System.err.println("-- Show Users");
                    users = getTaskMapper().getAllUsers();
                    req.setAttribute("users", users);
                    getViewManager().showUsers();
                    break;

                case ADMIN_ACTION_DEACTIVATE_USER:
                    VOUser user2 = new VOUser();
                    user2.setDNI(req.getParameter("item"));

                    System.err.println("-- Deactivate User: "+user2.getDNI());
                    getTaskMapper().deactivateUser(user2);

                    users = getTaskMapper().getAllUsers();
                    req.setAttribute("users", users);
                    getViewManager().showUsers();
                    break;

                case ADMIN_ACTION_ACTIVATE_USER:
                    VOUser user3 = new VOUser();
                    user3.setDNI(req.getParameter("item"));

                    System.err.println("-- Activate User: "+user3.getDNI());
                    getTaskMapper().activateUser(user3);

                    users = getTaskMapper().getAllUsers();
                    req.setAttribute("users", users);
                    getViewManager().showUsers();
                    break;

                case ADMIN_ACTION_EDIT_USER:
                    VOUser user4 = new VOUser();
                    user4.setDNI(req.getParameter("item"));

                    System.err.println("-- Edit User: "+user4.getDNI());
                    if (user4.getDNI().equals("new")){
                        req.setAttribute("euser", user4);
                        getViewManager().showEditUsers();
                    } else {
                        boolean allUser = getTaskMapper().getAllUser(user4);
                        if (allUser) {
                            System.err.println("-- El usuario está");
                            req.setAttribute("euser", user4);
                            getViewManager().showEditUsers();
                        } else {
                            System.err.println("-- El usuario no está");
                            req.setAttribute(PARAMETER_ERROR, "Los datos de usuario no son correctos o el usuario no existe. Por favor, inténtelo de nuevo.");
                            getViewManager().showError();
                        }
                    }
                    break;

                case ADMIN_ACTION_SAVE_USER:
                    VOUser user5 = new VOUser();
                    user5.setDNI(req.getParameter("id"));

                    System.err.println("-- Save User: "+user5.getDNI());

                    if (getTaskMapper().getAllUser(user5)){
                        user5.setFirstName(req.getParameter("name"));
                        user5.setLastName(req.getParameter("lastname"));
                        user5.seteMail(req.getParameter("email"));
                        String pass = req.getParameter("password");
                        if (pass.equals("")){
//                            System.out.println("Contraseña vacía");
                            user5.setPassword(null);
                        } else {
//                            System.out.println("Contraseña: "+pass);
                            user5.setPassword(pass);
                        }

//                        System.out.println("Active: "+req.getParameter("active")+" - Vip: "+req.getParameter("vip")+" - Tipo: "+req.getParameter("tipo"));
                        switch (Integer.parseInt(req.getParameter("tipo"))){
                            case 2:
                                user5.setTipo(MySQLContract.Tipo.normal);
                                break;
                            case 3:
                                user5.setTipo(MySQLContract.Tipo.admin);
                                break;
                        }

//                        if (req.getParameter("active")!=null){
//                            System.out.println("Active");
//                        } else {
//                            System.out.println("No active");
//                        }

                        user5.setActive(req.getParameter("active")!=null);
                        user5.setVip(req.getParameter("vip")!=null);

                        System.out.println(user5.toString());

                        getTaskMapper().updateUser(user5);

                        users = getTaskMapper().getAllUsers();
                        req.setAttribute("users", users);
                        getViewManager().showUsers();

                    } else {
                        System.err.println("[ERR] Not a Number");
                        req.setAttribute(PARAMETER_ERROR,"Not an user");
                        getViewManager().showError();
                    }
                    break;

                default:
                    shoppingCart=getTaskMapper().getAllCds();
                    req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

                    getViewManager().showIndex();
            }
        }catch (NullPointerException e){
            shoppingCart=getTaskMapper().getAllCds();
            req.setAttribute(VOShoppingCart.SESSION_ATTRIBUTE_CDS,shoppingCart);

            getViewManager().showIndex();
        }

    }
}

