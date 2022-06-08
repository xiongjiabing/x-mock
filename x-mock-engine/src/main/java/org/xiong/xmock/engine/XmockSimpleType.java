//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xiong.xmock.engine;

import java.util.Collection;
import java.util.Map;

import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.type.JavaType;

public final class XmockSimpleType extends TypeBase {
    protected final JavaType[] _typeParameters;
    protected final String[] _typeNames;

    protected XmockSimpleType(Class cls) {
        this(cls, (String[])null, (JavaType[])null, (Object)null, (Object)null);
    }

    /** @deprecated */
    @Deprecated
    protected XmockSimpleType(Class cls, String[] typeNames, JavaType[] typeParams) {
        this(cls, typeNames, typeParams, (Object)null, (Object)null);
    }

    protected XmockSimpleType(Class<?> cls, String[] typeNames, JavaType[] typeParams, Object valueHandler, Object typeHandler) {
        super(cls, 0, valueHandler, typeHandler);
        if (typeNames != null && typeNames.length != 0) {
            this._typeNames = typeNames;
            this._typeParameters = typeParams;
        } else {
            this._typeNames = null;
            this._typeParameters = null;
        }

    }

    public static XmockSimpleType constructUnsafe(Class<?> raw) {
        return new XmockSimpleType(raw, (String[])null, (JavaType[])null, (Object)null, (Object)null);
    }

    protected JavaType _narrow(Class<?> subclass) {
        return new XmockSimpleType(subclass, this._typeNames, this._typeParameters, this._valueHandler, this._typeHandler);
    }

    public JavaType narrowContentsBy(Class<?> subclass) {
        throw new IllegalArgumentException("Internal error: SimpleType.narrowContentsBy() should never be called");
    }

    public JavaType widenContentsBy(Class<?> subclass) {
        throw new IllegalArgumentException("Internal error: SimpleType.widenContentsBy() should never be called");
    }

    public static XmockSimpleType construct(Class<?> cls) {
        if (Map.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Map (class: " + cls.getName() + ")");
        } else if (Collection.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Collection (class: " + cls.getName() + ")");
        } else if (cls.isArray()) {
            throw new IllegalArgumentException("Can not construct SimpleType for an array (class: " + cls.getName() + ")");
        } else {
            return new XmockSimpleType(cls);
        }
    }

    public XmockSimpleType withTypeHandler(Object h) {
        return new XmockSimpleType(this._class, this._typeNames, this._typeParameters, this._valueHandler, h);
    }

    public JavaType withContentTypeHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenTypeHandler()");
    }

    public XmockSimpleType withValueHandler(Object h) {
        return h == this._valueHandler ? this : new XmockSimpleType(this._class, this._typeNames, this._typeParameters, h, this._typeHandler);
    }

    public XmockSimpleType withContentValueHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenValueHandler()");
    }

    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._typeParameters != null && this._typeParameters.length > 0) {
            sb.append('<');
            boolean first = true;
            JavaType[] arr$ = this._typeParameters;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                JavaType t = arr$[i$];
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }

                sb.append(t.toCanonical());
            }

            sb.append('>');
        }

        return sb.toString();
    }

    public boolean isContainerType() {
        return false;
    }

    public int containedTypeCount() {
        return this._typeParameters == null ? 0 : this._typeParameters.length;
    }

    public JavaType containedType(int index) {
        return index >= 0 && this._typeParameters != null && index < this._typeParameters.length ? this._typeParameters[index] : null;
    }

    public String containedTypeName(int index) {
        return index >= 0 && this._typeNames != null && index < this._typeNames.length ? this._typeNames[index] : null;
    }

    public StringBuilder getErasedSignature(StringBuilder sb) {
        return _classSignature(this._class, sb, true);
    }

    public StringBuilder getGenericSignature(StringBuilder sb) {
        _classSignature(this._class, sb, false);
        if (this._typeParameters != null) {
            sb.append('<');
            JavaType[] arr$ = this._typeParameters;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                JavaType param = arr$[i$];
                sb = param.getGenericSignature(sb);
            }

            sb.append('>');
        }

        sb.append(';');
        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("[simple type, class ").append(this.buildCanonicalName()).append(']');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o.getClass() != this.getClass()) {
            return false;
        } else {
            XmockSimpleType other = (XmockSimpleType)o;
            if (other._class != this._class) {
                return false;
            } else {
                JavaType[] p1 = this._typeParameters;
                JavaType[] p2 = other._typeParameters;
                if (p1 != null) {
                    if (p2 == null) {
                        return false;
                    } else if (p1.length != p2.length) {
                        return false;
                    } else {
                        int i = 0;

                        for(int len = p1.length; i < len; ++i) {
                            if (!p1[i].equals(p2[i])) {
                                return false;
                            }
                        }

                        return true;
                    }
                } else {
                    return p2 == null || p2.length == 0;
                }
            }
        }
    }
}
