package org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import java.util.Objects;
import java.util.List;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput
 *
 */
public class MigrateNetworkPathInputBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput> {

    private java.lang.Integer _flowPriority;
    private java.lang.Integer _hardTimeout;
    private java.lang.Integer _idleTimeout;
    private java.lang.String _newDstIpAddress;
    private java.lang.String _newSrcIpAddress;
    private java.lang.String _oldDstIpAddress;
    private java.lang.String _oldSrcIpAddress;
    private List<java.lang.Integer> _switchesInNewPath;
    private List<java.lang.Integer> _switchesInOldPath;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> augmentation = Collections.emptyMap();

    public MigrateNetworkPathInputBuilder() {
    }

    public MigrateNetworkPathInputBuilder(MigrateNetworkPathInput base) {
        this._flowPriority = base.getFlowPriority();
        this._hardTimeout = base.getHardTimeout();
        this._idleTimeout = base.getIdleTimeout();
        this._newDstIpAddress = base.getNewDstIpAddress();
        this._newSrcIpAddress = base.getNewSrcIpAddress();
        this._oldDstIpAddress = base.getOldDstIpAddress();
        this._oldSrcIpAddress = base.getOldSrcIpAddress();
        this._switchesInNewPath = base.getSwitchesInNewPath();
        this._switchesInOldPath = base.getSwitchesInOldPath();
        if (base instanceof MigrateNetworkPathInputImpl) {
            MigrateNetworkPathInputImpl impl = (MigrateNetworkPathInputImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }


    public java.lang.Integer getFlowPriority() {
        return _flowPriority;
    }
    
    public java.lang.Integer getHardTimeout() {
        return _hardTimeout;
    }
    
    public java.lang.Integer getIdleTimeout() {
        return _idleTimeout;
    }
    
    public java.lang.String getNewDstIpAddress() {
        return _newDstIpAddress;
    }
    
    public java.lang.String getNewSrcIpAddress() {
        return _newSrcIpAddress;
    }
    
    public java.lang.String getOldDstIpAddress() {
        return _oldDstIpAddress;
    }
    
    public java.lang.String getOldSrcIpAddress() {
        return _oldSrcIpAddress;
    }
    
    public List<java.lang.Integer> getSwitchesInNewPath() {
        return _switchesInNewPath;
    }
    
    public List<java.lang.Integer> getSwitchesInOldPath() {
        return _switchesInOldPath;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public MigrateNetworkPathInputBuilder setFlowPriority(final java.lang.Integer value) {
        this._flowPriority = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setHardTimeout(final java.lang.Integer value) {
        this._hardTimeout = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setIdleTimeout(final java.lang.Integer value) {
        this._idleTimeout = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setNewDstIpAddress(final java.lang.String value) {
        this._newDstIpAddress = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setNewSrcIpAddress(final java.lang.String value) {
        this._newSrcIpAddress = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setOldDstIpAddress(final java.lang.String value) {
        this._oldDstIpAddress = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setOldSrcIpAddress(final java.lang.String value) {
        this._oldSrcIpAddress = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setSwitchesInNewPath(final List<java.lang.Integer> value) {
        this._switchesInNewPath = value;
        return this;
    }
    
     
    public MigrateNetworkPathInputBuilder setSwitchesInOldPath(final List<java.lang.Integer> value) {
        this._switchesInOldPath = value;
        return this;
    }
    
    public MigrateNetworkPathInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public MigrateNetworkPathInputBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    public MigrateNetworkPathInput build() {
        return new MigrateNetworkPathInputImpl(this);
    }

    private static final class MigrateNetworkPathInputImpl implements MigrateNetworkPathInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput.class;
        }

        private final java.lang.Integer _flowPriority;
        private final java.lang.Integer _hardTimeout;
        private final java.lang.Integer _idleTimeout;
        private final java.lang.String _newDstIpAddress;
        private final java.lang.String _newSrcIpAddress;
        private final java.lang.String _oldDstIpAddress;
        private final java.lang.String _oldSrcIpAddress;
        private final List<java.lang.Integer> _switchesInNewPath;
        private final List<java.lang.Integer> _switchesInOldPath;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> augmentation = Collections.emptyMap();

        private MigrateNetworkPathInputImpl(MigrateNetworkPathInputBuilder base) {
            this._flowPriority = base.getFlowPriority();
            this._hardTimeout = base.getHardTimeout();
            this._idleTimeout = base.getIdleTimeout();
            this._newDstIpAddress = base.getNewDstIpAddress();
            this._newSrcIpAddress = base.getNewSrcIpAddress();
            this._oldDstIpAddress = base.getOldDstIpAddress();
            this._oldSrcIpAddress = base.getOldSrcIpAddress();
            this._switchesInNewPath = base.getSwitchesInNewPath();
            this._switchesInOldPath = base.getSwitchesInOldPath();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>singletonMap(e.getKey(), e.getValue());
                break;
            default :
                this.augmentation = new HashMap<>(base.augmentation);
            }
        }

        @Override
        public java.lang.Integer getFlowPriority() {
            return _flowPriority;
        }
        
        @Override
        public java.lang.Integer getHardTimeout() {
            return _hardTimeout;
        }
        
        @Override
        public java.lang.Integer getIdleTimeout() {
            return _idleTimeout;
        }
        
        @Override
        public java.lang.String getNewDstIpAddress() {
            return _newDstIpAddress;
        }
        
        @Override
        public java.lang.String getNewSrcIpAddress() {
            return _newSrcIpAddress;
        }
        
        @Override
        public java.lang.String getOldDstIpAddress() {
            return _oldDstIpAddress;
        }
        
        @Override
        public java.lang.String getOldSrcIpAddress() {
            return _oldSrcIpAddress;
        }
        
        @Override
        public List<java.lang.Integer> getSwitchesInNewPath() {
            return _switchesInNewPath;
        }
        
        @Override
        public List<java.lang.Integer> getSwitchesInOldPath() {
            return _switchesInOldPath;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + Objects.hashCode(_flowPriority);
            result = prime * result + Objects.hashCode(_hardTimeout);
            result = prime * result + Objects.hashCode(_idleTimeout);
            result = prime * result + Objects.hashCode(_newDstIpAddress);
            result = prime * result + Objects.hashCode(_newSrcIpAddress);
            result = prime * result + Objects.hashCode(_oldDstIpAddress);
            result = prime * result + Objects.hashCode(_oldSrcIpAddress);
            result = prime * result + Objects.hashCode(_switchesInNewPath);
            result = prime * result + Objects.hashCode(_switchesInOldPath);
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
            if (!org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput other = (org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput)obj;
            if (!Objects.equals(_flowPriority, other.getFlowPriority())) {
                return false;
            }
            if (!Objects.equals(_hardTimeout, other.getHardTimeout())) {
                return false;
            }
            if (!Objects.equals(_idleTimeout, other.getIdleTimeout())) {
                return false;
            }
            if (!Objects.equals(_newDstIpAddress, other.getNewDstIpAddress())) {
                return false;
            }
            if (!Objects.equals(_newSrcIpAddress, other.getNewSrcIpAddress())) {
                return false;
            }
            if (!Objects.equals(_oldDstIpAddress, other.getOldDstIpAddress())) {
                return false;
            }
            if (!Objects.equals(_oldSrcIpAddress, other.getOldSrcIpAddress())) {
                return false;
            }
            if (!Objects.equals(_switchesInNewPath, other.getSwitchesInNewPath())) {
                return false;
            }
            if (!Objects.equals(_switchesInOldPath, other.getSwitchesInOldPath())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                MigrateNetworkPathInputImpl otherImpl = (MigrateNetworkPathInputImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.activesdn.rev150601.MigrateNetworkPathInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("MigrateNetworkPathInput [");
            boolean first = true;
        
            if (_flowPriority != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_flowPriority=");
                builder.append(_flowPriority);
             }
            if (_hardTimeout != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_hardTimeout=");
                builder.append(_hardTimeout);
             }
            if (_idleTimeout != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_idleTimeout=");
                builder.append(_idleTimeout);
             }
            if (_newDstIpAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_newDstIpAddress=");
                builder.append(_newDstIpAddress);
             }
            if (_newSrcIpAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_newSrcIpAddress=");
                builder.append(_newSrcIpAddress);
             }
            if (_oldDstIpAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_oldDstIpAddress=");
                builder.append(_oldDstIpAddress);
             }
            if (_oldSrcIpAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_oldSrcIpAddress=");
                builder.append(_oldSrcIpAddress);
             }
            if (_switchesInNewPath != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_switchesInNewPath=");
                builder.append(_switchesInNewPath);
             }
            if (_switchesInOldPath != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_switchesInOldPath=");
                builder.append(_switchesInOldPath);
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
