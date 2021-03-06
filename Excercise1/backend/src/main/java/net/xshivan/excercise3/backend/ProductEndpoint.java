/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package net.xshivan.excercise3.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "productApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.excercise3.xshivan.net",
                ownerName = "backend.excercise3.xshivan.net",
                packagePath = ""
        )
)
public class ProductEndpoint {

    private final String PROD_PARENT_KEY = "productParent";

    private final String PROD_KEY = "product";

    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "putProduct")
    public ProductBean putProduct(ProductBean productBean) {

        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            //Key productBeanParentKey = KeyFactory.createKey(PROD_PARENT_KEY, PROD_PARENT_KEY);
            KeyRange keyRange = datastoreService.allocateIds(PROD_KEY, 1L);
            Entity productBeanEntity = new Entity(keyRange.getStart());

            productBeanEntity.setProperty("name", productBean.getName());
            productBeanEntity.setProperty("isPurchased", productBean.getIsPurchased());

            datastoreService.put(productBeanEntity);
            txn.commit();

            productBean.setId(productBeanEntity.getKey().getId());
        } finally {
            if (txn.isActive())
                txn.rollback();
        }

        return productBean;
    }

    @ApiMethod(name = "deleteProduct")
    public void deleteProduct(@Named("productId") Long productId) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();

        try {
            Key key = KeyFactory.createKey(PROD_KEY, productId);

            Entity entity = datastoreService.get(key);
            datastoreService.delete(entity.getKey());
            txn.commit();
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (txn.isActive())
                txn.rollback();
        }
    }

    @ApiMethod(name = "fetchProducts")
    public ArrayList<ProductBean> fetchProducts() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

        Query query = new Query(PROD_KEY);
        List<Entity> results = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

        ArrayList<ProductBean> productBeans = new ArrayList<>();

        for (Entity entity : results) {
            ProductBean productBean = new ProductBean();

            productBean.setId(entity.getKey().getId());
            productBean.setIsPurchased((Boolean)entity.getProperty("isPurchased"));
            productBean.setName((String)entity.getProperty("name"));

            productBeans.add(productBean);
        }

        return productBeans;
    }
}
