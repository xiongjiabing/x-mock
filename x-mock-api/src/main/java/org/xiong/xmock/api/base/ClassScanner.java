package org.xiong.xmock.api.base;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isBlank;


public enum ClassScanner {
	;
	public static enum ProtocolTypes {
		http, https, file, jar;
	}


	/**
	 *
	 * 加载指定路径下的class
	 * @param urls
	 * @return Class
	 */
	public static List<Class> loadClassByUrls(URL[] urls){
		List<Class> classList = new LinkedList<Class>();
		try{
			for (URL url: urls) {

				File fileJar = new File(url.toURI());
				if(!fileJar.getPath().endsWith(".jar")){
					continue;
				}

				String jarPath = url.getFile();
				if( jarPath.lastIndexOf("!") != -1){
					jarPath = jarPath.substring(0, jarPath.lastIndexOf("!")).replaceFirst("file:", "");
				}
				classList.addAll(recursiveScan4Jar("", jarPath));
			}
		}catch (Exception e){
		}
		return classList;
	}

	/**
	 * Find all classes in packages 扫描一或多个包下的所有Class，包含接口类
	 *
	 * @param scanBasePackages
	 * @return
	 */
	public static List<Class> scanPackages(String... scanBasePackages) {
		List<Class> classList = new LinkedList<Class>();
		if (scanBasePackages.length == 0) {
			return classList;
		}
		for (String pkg : scanBasePackages) {
			if (pkg != null && pkg.length() != 0)
				classList.addAll(ClassScanner.scanOnePackage(pkg,true));
		}
		return classList;
	}


	/**
	 * Find all classes with given annotation in packages 扫描某个包下带有注解的Class
	 *
	 * @param anno
	 * @param scanBasePackages
	 * @return
	 */
	public static List<Class> scanByAnno(Class<? extends Annotation> anno, String... scanBasePackages) {
		List<Class> classList = scanPackagesNojar(scanBasePackages);
		List<Class> result = new ArrayList<Class>();
		for (Class clz : classList) {
			Annotation clzAnno = clz.getAnnotation(anno);
			if (clzAnno != null)
				result.add(clz);
		}
		return result;
	}


	public static List<Class> scanPackagesNojar(String... scanBasePackages) {
		List<Class> classList = new LinkedList<Class>();
		if (scanBasePackages.length == 0) {
			return classList;
		}
		for (String pkg : scanBasePackages) {
			if (pkg != null && pkg.length() != 0)
				classList.addAll(ClassScanner.scanOnePackage(pkg,true));
		}
		return classList;
	}



	/**
	 * Find all classes with given name patten 扫描某个包下所有类名匹配通配符的Class
	 *
	 * @param nameSimpleReg
	 *            name patten, only 1 * allow, 类名简化版通配符，只允许一个星号出现
	 * @param scanBasePackages
	 * @return
	 */
//	public static List<Class> scanByName(String nameSimpleReg, String... scanBasePackages) {
//		List<Class> classList = scanPackages(scanBasePackages);
//		List<Class> result = new ArrayList<Class>();
//		for (Class clz : classList)
//			if (NameMatchUtil.nameMatch(nameSimpleReg, clz.getName()))
//				result.add(clz);
//		return result;
//	}

	/**
	 * find all classes in one package 扫描某个包下所有Class类
	 *
	 * @param pkg
	 * @return Class
	 */
	private static List<Class> scanOnePackage(String pkg,boolean isScanJar) {
		List<Class> classList = new LinkedList<Class>();
		try {
			// 包名转化为路径名
			String pathName = package2Path(pkg);
			// 获取路径下URL
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathName);
			// 循环扫描路径
			classList = scanUrls(pkg, urls,isScanJar);
		} catch (IOException e) {
			System.err.println("Warning: Can not scan package：" + pkg);
		}

		return classList;
	}

	/**
	 * find all classes in urls 扫描多个Url路径，找出符合包名的Class类
	 *
	 * @param pkg
	 * @param urls
	 * @return Class
	 * @throws IOException
	 */
	private static List<Class> scanUrls(String pkg, Enumeration<URL> urls,boolean isScanJar) throws IOException {
		List<Class> classList = new LinkedList<Class>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			// 获取协议
			String protocol = url.getProtocol();

			if (ProtocolTypes.file.name().equals(protocol)) {
				// 文件
				String path = URLDecoder.decode(url.getFile(), "UTF-8");
				classList.addAll(recursiveScan4Path(pkg, path));

			} else if (ProtocolTypes.jar.name().equals(protocol) && isScanJar) {
				// jar包
				String jarPath = getJarPathFormUrl(url);
				classList.addAll(recursiveScan4Jar(pkg, jarPath));
			}
		}
		return classList;
	}



	public static List<String> scanOnePkg(String pkg) {
		List<String> classList = new LinkedList<>();
		try {
			// 包名转化为路径名
			String pathName = package2Path(pkg);
			// 获取路径下URL
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pathName);
			// 循环扫描路径
			classList = scanFilePath(pkg, urls);
		} catch (IOException e) {
			System.err.println("Warning: Can not scan pkg：" + pkg);
		}

		return classList;
	}


	private static List<String> scanFilePath(String pkg, Enumeration<URL> urls) throws IOException {
		List<String> classList = new LinkedList<String>();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();

			// 获取协议
			String protocol = url.getProtocol();

			if (ProtocolTypes.file.name().equals(protocol)) {
				// 文件
				String path = URLDecoder.decode(url.getFile(), "UTF-8");
				classList.addAll(recursiveScanMockPath(pkg, path));
			}
		}
		return classList;
	}

	private static List<String> recursiveScanMockPath(String pkg, String filePath) {
		List<String> classList = new LinkedList<String>();

		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			return classList;
		}

		// 处理类文件
		File[] classes = file.listFiles(new FileFilter() {
			public boolean accept(File child) {
				if (child.getName() == null || child.getName().length() == 0) {
					return false;
				}
				return child.getName().endsWith(".mock");
			}
		});
		for (File child : classes) {
			try {
				classList.add(child.getPath().substring(child.getPath().indexOf("mockfile")));
			} catch (Exception e) {
			}
		}

		// 处理目录

		File[] dirs = file.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});

		for (File child : dirs) {
			String childPackageName = new StringBuilder().append(pkg).append(".").append(child.getName()).toString();
			String childPath = new StringBuilder().append(filePath).append("/").append(child.getName()).toString();
			classList.addAll(recursiveScanMockPath(childPackageName, childPath));
		}

		return classList;
	}



	/**
	 * get real path from url 从url中获取jar真实路径
	 * <p>
	 * jar文件url示例如下：
	 * <p>
	 * jar:file:/Users/cent/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.18.4/7103ab519b1cdbb0642ad4eaf1db209d905d0f96/lombok-1.18.4.jar!/org
	 *
	 * @param url
	 * @return
	 */
	public static String getJarPathFormUrl(URL url) {
		String file = url.getFile();
		String jarRealPath = file.substring(0, file.lastIndexOf("!")).replaceFirst("file:", "");
		return jarRealPath;
	}


	/**
	 * recursive scan for path 递归扫描指定文件路径下的Class文件
	 *
	 * @param pkg
	 * @param filePath
	 * @return Class列表
	 */
	private static List<Class> recursiveScan4Path(String pkg, String filePath) {
		List<Class> classList = new LinkedList<Class>();

		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			return classList;
		}

		// 处理类文件
		File[] classes = file.listFiles(new FileFilter() {
			public boolean accept(File child) {
				return isClass(child.getName());
			}
		});
		for (File child : classes) {
			String className = classFile2SimpleClass(
					new StringBuilder().append(pkg).append(".").append(child.getName()).toString());

			try {
				Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
				classList.add(clz);
			} catch (ClassNotFoundException e) {
				System.err.println("Warning: Can not load class:" + className);
			} catch (LinkageError e) {
				System.err.println("Warning: Can not load class:" + className);
			}

		}

		// 处理目录

		File[] dirs = file.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});

		for (File child : dirs) {
			String childPackageName = new StringBuilder().append(pkg).append(".").append(child.getName()).toString();
			String childPath = new StringBuilder().append(filePath).append("/").append(child.getName()).toString();
			classList.addAll(recursiveScan4Path(childPackageName, childPath));
		}

		return classList;
	}

	/**
	 * Recursive scan 4 jar 递归扫描Jar文件内的Class类
	 *
	 * @param pkg
	 * @param jarPath
	 * @return Class列表
	 * @throws IOException
	 */
	private static List<Class> recursiveScan4Jar(String pkg, String jarPath) throws IOException {
		List<Class> classList = new LinkedList<Class>();

		JarInputStream jin = new JarInputStream(new FileInputStream(jarPath));
		JarEntry entry = jin.getNextJarEntry();
		while (entry != null) {
			String name = entry.getName();
			entry = jin.getNextJarEntry();

			if (!name.contains(package2Path(pkg))) {
				continue;
			}
			if (isClass(name)) {
				if (isAnonymousInnerClass(name)) {
					// 是匿名内部类，跳过不作处理
					continue;
				}

				String className = classFile2SimpleClass(path2Package(name));
				try {
					Class clz = Thread.currentThread().getContextClassLoader().loadClass(className);
					classList.add(clz);
				} catch (ClassNotFoundException e) {
					System.err.println("Warning: Can not load class:" + className);
				} catch (LinkageError e) {
					System.err.println("Warning: Can not load class:" + className);
				}
			}
		}

		return classList;
	}

	// ===== Inside used static tool methods =====
	private static final Pattern ANONYMOUS_INNER_CLASS_PATTERN = Pattern.compile("^[\\s\\S]*\\${1}\\d+\\.class$");

	private static String package2Path(String packageName) {
		return packageName.replace(".", "/");
	}

	private static String path2Package(String pathName) {
		return pathName.replaceAll("/", ".");
	}

	private static boolean isClass(String fileName) {
		if (fileName == null || fileName.length() == 0) {
			return false;
		}
		return fileName.endsWith(".class");
	}

	private static String classFile2SimpleClass(String classFileName) {
		return classFileName.replace(".class", "");
	}

	private static boolean isAnonymousInnerClass(String className) {
		return ANONYMOUS_INNER_CLASS_PATTERN.matcher(className).matches();
	}

}