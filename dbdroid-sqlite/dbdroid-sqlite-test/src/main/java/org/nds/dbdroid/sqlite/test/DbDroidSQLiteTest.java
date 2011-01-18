package org.nds.dbdroid.sqlite.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.Assert;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.sqlite.SQLiteDataBaseManager;
import org.nds.dbdroid.sqlite.dao.Object1Dao;
import org.nds.dbdroid.sqlite.entity.Object1;
import org.nds.dbdroid.sqlite.test.dao.TestDao;
import org.nds.dbdroid.sqlite.test.entity.Test;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;
// import org.nds.package_info.ClassPathPackageInfo;
// import org.nds.package_info.ClassPathPackageInfoSource;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.test.AndroidTestCase;

public class DbDroidSQLiteTest extends AndroidTestCase {

    private static final Logger log = LoggerFactory.getLogger(DbDroidSQLiteTest.class);

    /*public void testPackageInfo() throws NameNotFoundException, ClassNotFoundException {
        // ask for the code of the foreign context to be included and to ignore any security given by the cross-process(owner) execution
        // in working-environment to error checking ...
        Context tmpCtxt = getContext().createPackageContext("org.nds.dbdroid.sqlite", Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);

        Class<?> c0 = tmpCtxt.getClassLoader().loadClass("org.nds.dbdroid.sqlite.DbDroidSQLite");
        Class<?> c = tmpCtxt.getClassLoader().loadClass("org.nds.dbdroid.sqlite.test.dao.TestDao");
        // do normal Java-Reflection things with c

        // Apk paths used to search for test classes when using
        // TestSuiteBuilders.
        getContext().getPackageManager().getSystemSharedLibraryNames();
        getContext().fileList();
        getContext().getDir(".", Context.MODE_WORLD_READABLE);
        getContext().getApplicationContext().getPackageManager().getSystemSharedLibraryNames();
        getContext().getApplicationContext().fileList();
        getContext().getApplicationContext().getDir(".", Context.MODE_WORLD_READABLE);
        String[] apkPaths = { getContext().getPackageName(), getContext().getApplicationContext().getPackageName(),
                getContext().getCacheDir().getAbsolutePath(), getContext().getFilesDir().getAbsolutePath(),
                getContext().getApplicationContext().getCacheDir().getAbsolutePath(),
                getContext().getApplicationContext().getFilesDir().getAbsolutePath() };
        ClassPathPackageInfoSource.setApkPaths(apkPaths);

        ClassPathPackageInfoSource classPathSource = new ClassPathPackageInfoSource();
        // classPathSource.setClassLoader(tmpCtxt.getClassLoader());

        ClassPathPackageInfo cppi = classPathSource.getPackageInfo("org.nds.dbdroid.sqlite.test.dao");
        System.out.println(cppi.getPackageName() + ": " + cppi.getTopLevelClassesRecursive());

        for (ClassPathPackageInfo packageInfo : cppi.getSubpackages()) {
            System.out.println(packageInfo.getPackageName() + ": " + packageInfo.getTopLevelClassesRecursive());
        }
    }*/

    public void testPersistence() throws IOException, DBDroidException, NameNotFoundException {
        // ask for the code of the foreign context to be included and to ignore any security given by the cross-process(owner) execution
        // in working-environment to error checking ...
        Context ctx = getContext().createPackageContext("org.nds.dbdroid.sqlite", Context.CONTEXT_INCLUDE_CODE + Context.CONTEXT_IGNORE_SECURITY);

        InputStream config = getContext().getAssets().open("dbdroid/dbdroid.xml");

        DataBaseManager dbManager = new SQLiteDataBaseManager(config, getContext(), "dbdroid/test-sqlite.db", null, 1);
        dbManager.setClassLoader(ctx.getClassLoader());
        dbManager.open();

        Object1Dao object1Dao = dbManager.getDAO(Object1Dao.class);
        List<Object1> object1List = object1Dao.findAll();
        Assert.assertTrue("Object1 objects list is not empty: " + object1List, object1List.isEmpty());

        TestDao testDao = dbManager.getDAO(TestDao.class);
        List<Test> testList = testDao.findAll();
        Assert.assertTrue("Test objects list is not empty: " + testList, testList.isEmpty());

        Test test = new Test("Nicolas");
        test = dbManager.saveOrUpdate(test);
        log.debug("Entity saved: " + test.get_id());
        Test test2 = testDao.findById(String.valueOf(test.get_id()));
        Assert.assertNotNull("Test object is null: " + test2, test2);
        Assert.assertEquals("Nicolas", test2.getName());

        /////////

        Object1 o1 = new Object1("toto");
        o1 = dbManager.saveOrUpdate(o1);
        log.debug("Entity saved: " + o1.get_id());

        Object1 o2 = new Object1("titi");
        o2 = dbManager.saveOrUpdate(o2);
        log.debug("Entity saved: " + o2.get_id());

        Query query = dbManager.createQuery(Object1.class);
        List<Object1> list = (List<Object1>) query.queryList();
        Assert.assertTrue("Object1 objects list is empty: " + list, !list.isEmpty());
        Assert.assertEquals(2, list.size());
        for (Object1 o : list) {
            log.debug(o.get_id() + ": " + o.getName());
        }
        dbManager.delete(list.get(0));

        Integer id = list.get(1).get_id();
        String name = list.get(1).getName();

        list = object1Dao.findAll();
        Assert.assertTrue("Object1 objects list is empty: " + list, !list.isEmpty());
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(id, list.get(0).get_id());
        Assert.assertEquals(name, list.get(0).getName());

        dbManager.close();
    }
}
