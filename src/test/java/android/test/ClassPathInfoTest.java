package android.test;

import org.junit.Test;
import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.package_info.ClassPathPackageInfo;
import org.nds.package_info.ClassPathPackageInfoSource;

public class ClassPathInfoTest {

    @Test
    public void testPackageInfo() {
        ClassPathPackageInfoSource classPathSource = new ClassPathPackageInfoSource();
        // classPathSource.setClassLoader(Dao1.class.getClassLoader());

        ClassPathPackageInfo cppi = classPathSource.getPackageInfo("org.nds.dbdroid.dao");
        for (Class<?> clazz : cppi.getTopLevelClassesRecursive()) {
            boolean isDAO = IAndroidDAO.class.isAssignableFrom(clazz);
            System.out.println("Class " + clazz.getCanonicalName() + (isDAO ? " is a DAO" : " is NOT a DAO"));
        }

        for (ClassPathPackageInfo packageInfo : cppi.getSubpackages()) {
            System.out.println(packageInfo.getPackageName() + ": " + packageInfo.getTopLevelClassesRecursive());
        }
    }

}
