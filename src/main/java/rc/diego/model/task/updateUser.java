package rc.diego.model.task;

import rc.diego.model.VO.VOUser;
import rc.diego.model.persistence.DataManager;
import rc.diego.model.persistence.InterfaceDAOFactory;

import java.sql.SQLException;

;

/**
 * Created by entakitos on 17/03/16.
 */
public class updateUser implements InterfaceTask{
    private VOUser users;

    public VOUser getUser() {
        return users;
    }

    public updateUser setUser(VOUser cd) {
        this.users = cd;
        return this;
    }

    @Override
    public void run() {

        try {
            DataManager.getDAOUsers().updateUser(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
