package android.test;

import org.junit.Test;
import org.nds.package_info.ClassPathPackageInfo;
import org.nds.package_info.ClassPathPackageInfoSource;

public class ClassPathInfoTest {

    @Test
    public void testPackageInfo() {
        ClassPathPackageInfoSource classPathSource = new ClassPathPackageInfoSource();
        // classPathSource.setClassLoader(Dao1.class.getClassLoader());

        ClassPathPackageInfo cppi = classPathSource.getPackageInfo("org.nds.dbdroid.dao");
        System.out.println(cppi.getPackageName() + ": " + cppi.getTopLevelClassesRecursive());

        for (ClassPathPackageInfo packageInfo : cppi.getSubpackages()) {
            System.out.println(packageInfo.getPackageName() + ": " + packageInfo.getTopLevelClassesRecursive());
        }
    }

}
