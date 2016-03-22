package client;

import java.io.Serializable;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;

/**
 *
 * @author George Kravas
 */

public class MLObject
    implements Serializable, ManagedObject {
        /** The version of the serialized form of this class. */
        private static final long serialVersionUID = 1L;
        /** The name of this object. */
        private String name;
        /** The description of this object. */
        private String description;

        public MLObject() {}

        public MLObject(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public void setName(String name) {
            AppContext.getDataManager().markForUpdate(this);
            this.name = name;
        }

        public void setDescription(String description) {
            AppContext.getDataManager().markForUpdate(this);
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return getName();
        }
}
