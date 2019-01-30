package com.dianping.shield.bridge

import android.util.Log

/**
 * Created by zhi.he on 2018/5/14.
 */
open class ShieldLogger {

    companion object {
        /**
         * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
         */
        @JvmStatic
        val VERBOSE = Log.VERBOSE
        /**
         * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
         */
        @JvmStatic
        val DEBUG = Log.DEBUG
        /**
         * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
         */
        @JvmStatic
        val INFO = Log.INFO
        /**
         * w/warning：用以打印不太正常但是还不是错误的日志。
         */
        @JvmStatic
        val WARN = Log.WARN
        /**
         * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
         */
        @JvmStatic
        val ERROR = Log.ERROR
    }

    /**
     * 当前的输出等级，当n>=LEVEL时才会输出<br></br>
     * 如果当前为调试模式，默认应该设置为i/info<br></br>
     * 如果想完全禁用输出，如发布，则应该设置为Integer.MAX_VALUE<br></br>
     * 默认为完全禁用
     */
    private val TAG_DEFAULT = "S.H.I.E.L.D"
    var level: Int = Int.MAX_VALUE

    /**
     * 检查当前是否需要输出对应的level。
     *
     *
     * 如果直接输出字符串，则无需检查。如Log.d("test", "this is a error");
     */
    open fun isLoggable(level: Int): Boolean {
        return level >= this.level
    }

    /**
     * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
     */
    @JvmOverloads
    open fun v(tag: String? = TAG_DEFAULT, log: String?, vararg obj: Any?) {
        if (VERBOSE >= level) {
            Log.v(tag, log?.let { String.format(it, *obj) })
        }
    }

    /**
     * v/verbose：用以打印非常详细的日志，例如如果你需要打印网络请求及返回的数据。
     *
     *
     * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
     */
    @JvmOverloads
    open fun v(tag: String? = TAG_DEFAULT, log: String?, e: Throwable, vararg obj: Any?) {
        if (VERBOSE >= level) {
            Log.v(tag, log?.let { String.format(it, *obj) }, e)
        }
    }


    /**
     * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
     */
    @JvmOverloads
    open fun d(tag: String? = TAG_DEFAULT, log: String?, vararg obj: Any?) {
        if (DEBUG >= level) {
            Log.d(tag, log?.let { String.format(it, *obj) })
        }
    }

    /**
     * d/debug：用以打印便于调试的日志，例如网络请求返回的关键结果或者操作是否成功。
     *
     *
     * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
     */
    @JvmOverloads
    open fun d(tag: String? = TAG_DEFAULT, log: String?, e: Throwable, vararg obj: Any?) {
        if (DEBUG >= level) {
            Log.d(tag, log?.let { String.format(it, *obj) }, e)
        }
    }

    /**
     * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
     */
    @JvmOverloads
    open fun i(tag: String? = TAG_DEFAULT, log: String?, vararg obj: Any?) {
        if (INFO >= level) {
            Log.i(tag, log?.let { String.format(it, *obj) })
        }
    }

    /**
     * i/information：用以打印为以后调试或者运行中提供运行信息的日志，例如进入或退出了某个函数、进入了函数的某个分支等。
     *
     *
     * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
     */
    @JvmOverloads
    open fun i(tag: String? = TAG_DEFAULT, log: String?, e: Throwable, vararg obj: Any?) {
        if (INFO >= level) {
            Log.i(tag, log?.let { String.format(it, *obj) }, e)
        }
    }

    /**
     * w/warning：用以打印不太正常但是还不是错误的日志。
     */
    @JvmOverloads
    open fun w(tag: String? = TAG_DEFAULT, log: String?, vararg obj: Any?) {
        if (WARN >= level) {
            Log.w(tag, log?.let { String.format(it, *obj) })
        }
    }

    /**
     * w/warning：用以打印不太正常但是还不是错误的日志。
     *
     *
     * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
     */
    @JvmOverloads
    open fun w(tag: String? = TAG_DEFAULT, log: String?, e: Throwable, vararg obj: Any?) {
        if (WARN >= level) {
            Log.w(tag, log?.let { String.format(it, *obj) }, e)
        }
    }

    /**
     * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
     */
    @JvmOverloads
    open fun e(tag: String? = TAG_DEFAULT, log: String?, vararg obj: Any?) {
        if (ERROR >= level) {
            Log.e(tag, log?.let { String.format(it, *obj) })
        }
    }

    /**
     * e/error：用以打印出现错误的日志，一般用以表示错误导致功能无法继续运行。
     *
     *
     * 附带具体的Exception，一般必须带有tag。不允许在默认的tag中输出Exception
     */
    @JvmOverloads
    open fun e(tag: String? = TAG_DEFAULT, log: String?, e: Throwable, vararg obj: Any?) {
        if (ERROR >= level) {
            Log.e(tag, log?.let { String.format(it, *obj) }, e)
        }
    }

    /**
     * info log, it will be saved to file or sqlite.
     * Note:
     * if log's lenght over @LogConfig.MAX_LENGTH_SINGLE_LOG, it will be led to cut off.
     * if subTag's lenght over @LogConfig.MAX_LENGTH_TAG, it will be led to cut off.
     *
     *
     * Log output format:
     * clazz's simple name::subTag: log content
     *
     * @param clazz   clazz's simple name is tag.
     * @param subTag  custom tag
     * @param message log content
     */
    @JvmOverloads
    open fun codeLogInfo(clazz: Class<*>?, message: String?, subTag: String? = "") {
        //default implement
        Log.i("${clazz?.simpleName}@$subTag", message)
    }

    /**
     * error log, it will be uploaded at once and saved to file or sqlite.
     * Note:
     * if log's lenght over @LogConfig.MAX_LENGTH_SINGLE_LOG, it will be led to cut off.
     * if subTag's lenght over @LogConfig.MAX_LENGTH_TAG, it will be led to cut off.
     *
     *
     * Log output format:
     * clazz's simple name::subTag: log content
     *
     * @param clazz   clazz's simple name is tag.
     * @param subTag  custom tag
     * @param message log content
     */
    @JvmOverloads
    open fun codeLogError(clazz: Class<*>?, message: String?, subTag: String? = "") {
        //default implement
        Log.e("${clazz?.simpleName}@$subTag", message)
    }

}