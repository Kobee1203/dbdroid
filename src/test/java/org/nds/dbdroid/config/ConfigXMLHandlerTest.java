package org.nds.dbdroid.config;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.dao.IDao1;
import org.nds.dbdroid.dao.subpkg.Dao3;
import org.nds.dbdroid.entity.Entity1;
import org.nds.dbdroid.entity.Entity2;
import org.nds.dbdroid.entity.Entity3;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.mock.MockDataBaseManager;
import org.nds.dbdroid.service.Service1;

public class ConfigXMLHandlerTest {

    @Test
    public void testConfigParsing() {
        try {
            DataBaseManager dbManager = new MockDataBaseManager(getClass().getResourceAsStream("dbdroid.xml"));
            dbManager.setXmlConfigValidating(true);
            dbManager.open();

            IDao1 dao1 = dbManager.getDAO(IDao1.class);
            Assert.assertNotNull(dao1);
            Entity1 entity1 = dao1.findById(2);
            Assert.assertNotNull(entity1);
            Assert.assertEquals("name2", entity1.getName());
            List<Entity1> entities1 = dao1.findAll();
            Assert.assertNotNull(entities1);
            Assert.assertEquals(2, entities1.size());

            Entity2 entity2 = dbManager.findById("10", Entity2.class);
            Assert.assertNotNull(entity2);
            Assert.assertEquals(new Long(123456789), entity2.getTime());
            List<Entity2> entities2 = dbManager.findAll(Entity2.class);
            Assert.assertNotNull(entities2);
            Assert.assertEquals(3, entities2.size());

            Dao3 dao3 = dbManager.getDAO(Dao3.class);
            Entity3 entity3 = dao3.findById("20");
            Assert.assertNotNull(entity3);
            Assert.assertEquals("name_1", entity3.getName());
            Assert.assertArrayEquals("<tag1>field_value1</tag1>".getBytes(), entity3.getDocument());
            List<Entity3> entities3 = dao3.findAll();
            Assert.assertNotNull(entities3);
            Assert.assertEquals(2, entities3.size());

            // Service
            Service1 service1 = dbManager.getService(Service1.class);
            dao1 = service1.getDao1();
            Assert.assertNotNull(dao1);
            entity1 = dao1.findById(2);
            Assert.assertNotNull(entity1);
            Assert.assertEquals("name2", entity1.getName());
            entities1 = dao1.findAll();
            Assert.assertNotNull(entities1);
            Assert.assertEquals(2, entities1.size());

            dbManager.close();
        } catch (DBDroidException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
