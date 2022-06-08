//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.xiong.xmock.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

public class XmockTypeParser {
    final XmockTypeFactory _factory;

    public XmockTypeParser(XmockTypeFactory f) {
        this._factory = f;
    }

    public JavaType parse(String canonical) throws IllegalArgumentException {
        canonical = canonical.trim();
        XmockTypeParser.MyTokenizer tokens = new XmockTypeParser.MyTokenizer(canonical);
        JavaType type = this.parseType(tokens);
        if (tokens.hasMoreTokens()) {
            throw this._problem(tokens, "Unexpected tokens after complete type");
        } else {
            return type;
        }
    }

    protected JavaType parseType(XmockTypeParser.MyTokenizer tokens) throws IllegalArgumentException {
        if (!tokens.hasMoreTokens()) {
            throw this._problem(tokens, "Unexpected end-of-string");
        } else {
            Class<?> base = this.findClass(tokens.nextToken(), tokens);
            if (tokens.hasMoreTokens()) {
                String token = tokens.nextToken();
                if ("<".equals(token)) {
                    return this._factory._fromParameterizedClass(base, this.parseTypes(tokens));
                }

                tokens.pushBack(token);
            }

            return this._factory._fromClass(base, (XmockTypeBindings)null);
        }
    }

    protected List<JavaType> parseTypes(XmockTypeParser.MyTokenizer tokens) throws IllegalArgumentException {
        ArrayList types = new ArrayList();

        while(tokens.hasMoreTokens()) {
            types.add(this.parseType(tokens));
            if (!tokens.hasMoreTokens()) {
                break;
            }

            String token = tokens.nextToken();
            if (">".equals(token)) {
                return types;
            }

            if (!",".equals(token)) {
                throw this._problem(tokens, "Unexpected token '" + token + "', expected ',' or '>')");
            }
        }

        throw this._problem(tokens, "Unexpected end-of-string");
    }

    protected Class<?> findClass(String className, XmockTypeParser.MyTokenizer tokens) {
        try {
            return ClassUtil.findClass(className);
        } catch (Exception var4) {
            if (var4 instanceof RuntimeException) {
                throw (RuntimeException)var4;
            } else {
                throw this._problem(tokens, "Can not locate class '" + className + "', problem: " + var4.getMessage());
            }
        }
    }

    protected IllegalArgumentException _problem(XmockTypeParser.MyTokenizer tokens, String msg) {
        return new IllegalArgumentException("Failed to parse type '" + tokens.getAllInput() + "' (remaining: '" + tokens.getRemainingInput() + "'): " + msg);
    }

    static final class MyTokenizer extends StringTokenizer {
        protected final String _input;
        protected int _index;
        protected String _pushbackToken;

        public MyTokenizer(String str) {
            super(str, "<,>", true);
            this._input = str;
        }

        public boolean hasMoreTokens() {
            return this._pushbackToken != null || super.hasMoreTokens();
        }

        public String nextToken() {
            String token;
            if (this._pushbackToken != null) {
                token = this._pushbackToken;
                this._pushbackToken = null;
            } else {
                token = super.nextToken();
            }

            this._index += token.length();
            return token;
        }

        public void pushBack(String token) {
            this._pushbackToken = token;
            this._index -= token.length();
        }

        public String getAllInput() {
            return this._input;
        }

        public String getUsedInput() {
            return this._input.substring(0, this._index);
        }

        public String getRemainingInput() {
            return this._input.substring(this._index);
        }
    }
}
