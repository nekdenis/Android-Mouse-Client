
package com.nek.airmouse.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.nek.airmouse.db.dto.ServerObj;

import java.sql.SQLException;

public class ServerDao extends BaseDaoImpl<ServerObj, Integer> {

    public ServerDao(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, ServerObj.class);
    }

//    public void create(final List<ServerObj> values) throws SQLException {
//        this.callBatchTasks(new Callable<Void>() {
//            @Override
//            public Void call() throws SQLException {
//                for (EventCategory item : values) {
//                    create(item);
//                }
//                return null;
//            }
//        });
//    }
//
//    public int deleteAll() throws SQLException {
//        DeleteBuilder<EventCategory, String> deleteBuilder = this.deleteBuilder();
//        return deleteBuilder.delete();
//    }
//
//    public EventCategory getBySystemName(String categorySystemName) throws SQLException {
//        List<EventCategory> result = queryForEq(EventCategory.SYSNAME_COLUMN, categorySystemName);
//        if (result.size() > 0) {
//            return result.get(0);
//        } else {
//            return null;
//        }
//    }
//
//    public List<EventCategory> getNotVip() throws SQLException {
//        return queryForEq(EventCategory.IS_VIP_COLUMN, false);
//    }
}
