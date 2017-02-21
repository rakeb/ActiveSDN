package org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import java.util.Objects;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput
 *
 */
public class MigratePathOutputBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput> {

    private java.lang.String _status;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> augmentation = Collections.emptyMap();

    public MigratePathOutputBuilder() {
    }

    public MigratePathOutputBuilder(MigratePathOutput base) {
        this._status = base.getStatus();
        if (base instanceof MigratePathOutputImpl) {
            MigratePathOutputImpl impl = (MigratePathOutputImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }


    public java.lang.String getStatus() {
        return _status;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public MigratePathOutputBuilder setStatus(final java.lang.String value) {
        this._status = value;
        return this;
    }
    
    public MigratePathOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public MigratePathOutputBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    public MigratePathOutput build() {
        return new MigratePathOutputImpl(this);
    }

    private static final class MigratePathOutputImpl implements MigratePathOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput.class;
        }

        private final java.lang.String _status;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> augmentation = Collections.emptyMap();

        private MigratePathOutputImpl(MigratePathOutputBuilder base) {
            this._status = base.getStatus();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>singletonMap(e.getKey(), e.getValue());
                break;
            default :
                this.augmentation = new HashMap<>(base.augmentation);
            }
        }

        @Override
        public java.lang.String getStatus() {
            return _status;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        private int hash = 0;
        private volatile boolean hashValid = false;
        
        @Override
        public int hashCode() {
            if (hashValid) {
                return hash;
            }
        
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hashCode(_status);
            result = prime * result + Objects.hashCode(augmentation);
        
            hash = result;
            hashValid = true;
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DataObject)) {
                return false;
            }
            if (!org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput other = (org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput)obj;
            if (!Objects.equals(_status, other.getStatus())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                MigratePathOutputImpl otherImpl = (MigratePathOutputImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.MigratePathOutput>> e : augmentation.entrySet()) {
                    if (!e.getValue().equals(other.getAugmentation(e.getKey()))) {
                        return false;
                    }
                }
                // .. and give the other one the chance to do the same
                if (!obj.equals(this)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public java.lang.String toString() {
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("MigratePathOutput [");
            boolean first = true;
        
            if (_status != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_status=");
                builder.append(_status);
             }
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("augmentation=");
            builder.append(augmentation.values());
            return builder.append(']').toString();
        }
    }

}