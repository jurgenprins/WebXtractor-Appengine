package com.bokella.webxtractor.server.util.dao;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class JDOUtil {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    public static PersistenceManagerFactory getPersistenceManagerFactory() {
        return pmfInstance;
    }
}
