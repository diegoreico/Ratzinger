package rc.diego.view.admin.products;

import rc.diego.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by entakitos on 20/02/16.
 */
public class stock extends AbstractView {

    public stock(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
        setViewUrl("/web/stocks.jsp");
    }
}
