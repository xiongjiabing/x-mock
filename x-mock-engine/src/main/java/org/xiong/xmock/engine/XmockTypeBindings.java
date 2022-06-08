//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xiong.xmock.engine;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.type.JavaType;

public class XmockTypeBindings {
    private static final JavaType[] NO_TYPES = new JavaType[0];
    public static final JavaType UNBOUND = new XmockSimpleType(Object.class);
    protected final XmockTypeFactory _typeFactory;
    protected final JavaType _contextType;
    protected final Class<?> _contextClass;
    protected Map<String, JavaType> _bindings;
    protected HashSet<String> _placeholders;
    private final XmockTypeBindings _parentBindings;

    public XmockTypeBindings(XmockTypeFactory typeFactory, Class cc) {
        this(typeFactory, (XmockTypeBindings)null, cc, (JavaType)null);
    }

    public XmockTypeBindings(XmockTypeFactory typeFactory, JavaType type) {
        this(typeFactory, (XmockTypeBindings)null, type.getRawClass(), type);
    }

    public XmockTypeBindings childInstance() {
        return new XmockTypeBindings(this._typeFactory, this, this._contextClass, this._contextType);
    }

    private XmockTypeBindings(XmockTypeFactory tf, XmockTypeBindings parent, Class<?> cc, JavaType type) {
        this._typeFactory = tf;
        this._parentBindings = parent;
        this._contextClass = cc;
        this._contextType = type;
    }

    public JavaType resolveType(Class<?> cls) {
        return this._typeFactory._constructType(cls, this);
    }

    public JavaType resolveType(Type type) {
        return this._typeFactory._constructType(type, this);
    }

    public int getBindingCount() {
        if (this._bindings == null) {
            this._resolve();
        }

        return this._bindings.size();
    }

    public JavaType findType(String name) {
        if (this._bindings == null) {
            this._resolve();
        }

        JavaType t = (JavaType)this._bindings.get(name);
        if (t != null) {
            return t;
        } else if (this._placeholders != null && this._placeholders.contains(name)) {
            return UNBOUND;
        } else if (this._parentBindings != null) {
            return this._parentBindings.findType(name);
        } else {
            if (this._contextClass != null) {
                Class<?> enclosing = this._contextClass.getEnclosingClass();
                if (enclosing != null && !Modifier.isStatic(this._contextClass.getModifiers())) {
                    return UNBOUND;
                }
            }

            String className;
            if (this._contextClass != null) {
                className = this._contextClass.getName();
            } else if (this._contextType != null) {
                className = this._contextType.toString();
            } else {
                className = "UNKNOWN";
            }

            throw new IllegalArgumentException("Type variable '" + name + "' can not be resolved (with context of class " + className + ")");
        }
    }

    public void addBinding(String name, JavaType type) {
        if (this._bindings == null || this._bindings.size() == 0) {
            this._bindings = new LinkedHashMap();
        }

        this._bindings.put(name, type);
    }

    public JavaType[] typesAsArray() {
        if (this._bindings == null) {
            this._resolve();
        }

        return this._bindings.size() == 0 ? NO_TYPES : (JavaType[])this._bindings.values().toArray(new JavaType[this._bindings.size()]);
    }

    protected void _resolve() {
        this._resolveBindings(this._contextClass);
        if (this._contextType != null) {
            int count = this._contextType.containedTypeCount();
            if (count > 0) {
                if (this._bindings == null) {
                    this._bindings = new LinkedHashMap();
                }

                for(int i = 0; i < count; ++i) {
                    String name = this._contextType.containedTypeName(i);
                    JavaType type = this._contextType.containedType(i);
                    this._bindings.put(name, type);
                }
            }
        }

        if (this._bindings == null) {
            this._bindings = Collections.emptyMap();
        }

    }

    public void _addPlaceholder(String name) {
        if (this._placeholders == null) {
            this._placeholders = new HashSet();
        }

        this._placeholders.add(name);
    }

    protected void _resolveBindings(Type t) {
        if (t != null) {
            Class raw;
            if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType)t;
                Type[] args = pt.getActualTypeArguments();
                if (args != null && args.length > 0) {
                    Class<?> rawType = (Class)pt.getRawType();
                    TypeVariable<?>[] vars = rawType.getTypeParameters();
                    if (vars.length != args.length) {
                        throw new IllegalArgumentException("Strange parametrized type (in class " + rawType.getName() + "): number of type arguments != number of type parameters (" + args.length + " vs " + vars.length + ")");
                    }

                    int i = 0;

                    for(int len = args.length; i < len; ++i) {
                        TypeVariable<?> var = vars[i];
                        String name = var.getName();
                        if (this._bindings == null) {
                            this._bindings = new LinkedHashMap();
                        } else if (this._bindings.containsKey(name)) {
                            continue;
                        }

                        this._addPlaceholder(name);
                        this._bindings.put(name, this._typeFactory._constructType(args[i], this));
                    }
                }

                raw = (Class)pt.getRawType();
            } else {
                if (!(t instanceof Class)) {
                    return;
                }

                raw = (Class)t;
                Class<?> decl = raw.getDeclaringClass();
                if (decl != null && !decl.isAssignableFrom(raw)) {
                    this._resolveBindings(raw.getDeclaringClass());
                }

                TypeVariable<?>[] vars = raw.getTypeParameters();
                if (vars != null && vars.length > 0) {
                    JavaType[] typeParams = null;
                    if (this._contextType != null && raw.isAssignableFrom(this._contextType.getRawClass())) {
                        typeParams = this._typeFactory.findTypeParameters(this._contextType, raw);
                    }

                    for(int i = 0; i < vars.length; ++i) {
                        TypeVariable<?> var = vars[i];
                        String name = var.getName();
                        Type varType = var.getBounds()[0];
                        if (varType != null) {
                            if (this._bindings == null) {
                                this._bindings = new LinkedHashMap();
                            } else if (this._bindings.containsKey(name)) {
                                continue;
                            }

                            this._addPlaceholder(name);
                            if (typeParams != null) {
                                this._bindings.put(name, typeParams[i]);
                            } else {
                                this._bindings.put(name, this._typeFactory._constructType(varType, this));
                            }
                        }
                    }
                }
            }

            this._resolveBindings(raw.getGenericSuperclass());
            Type[] arr$ = raw.getGenericInterfaces();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Type intType = arr$[i$];
                this._resolveBindings(intType);
            }

        }
    }

    public String toString() {
        if (this._bindings == null) {
            this._resolve();
        }

        StringBuilder sb = new StringBuilder("[TypeBindings for ");
        if (this._contextType != null) {
            sb.append(this._contextType.toString());
        } else {
            sb.append(this._contextClass.getName());
        }

        sb.append(": ").append(this._bindings).append("]");
        return sb.toString();
    }
}
