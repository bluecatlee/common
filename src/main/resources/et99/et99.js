

(function () {
    if (typeof Object.id == "undefined") {
        var id = 0;
        Object.id = function (o) {
            if (typeof o.__uniqueid == "undefined") {
                Object.defineProperty(o, "__uniqueid", {
                    value: ++id,
                    enumerable: false,
                    // This could go either way, depending on your 
                    // interpretation of what an "id" is
                    writable: false
                });
            }
            return o.__uniqueid;
        };
    }
})();

function HashTable() {
    var size = 0;
    var entry = new Object();

    this.add = function (key, value) {
        if (!this.containsKey(key)) {
            size++;
        }
        entry[key] = value;
    }

    this.getValue = function (key) {
        return this.containsKey(key) ? entry[key] : null;
    }

    this.remove = function (key) {
        if (this.containsKey(key) && (delete entry[key])) {
            size--;
        }
    }

    this.containsKey = function (key) {
        return (key in entry);
    }

    this.containsValue = function (value) {
        for (var prop in entry) {
            if (entry[prop] == value) {
                return true;
            }
        }
        return false;
    }

    this.getValues = function () {
        var values = new Array();
        for (var prop in entry) {
            values.push(entry[prop]);
        }
        return values;
    }

    this.getKeys = function () {
        var keys = new Array();
        for (var prop in entry) {
            keys.push(prop);
        }
        return keys;
    }

    this.getSize = function () {
        return size;
    }

    this.clear = function () {
        size = 0;
        entry = new Object();
    }
}

/**
 * 支持IE控件的示例
 * 共通部分不需要修改，仅修改注释“调用控件接口的部分”之后的内容
 * 须在注释“调用控件接口的部分”之后加入对控件接口的转发调用
 */
function ET99(clsid) {
    // 共通部分不要修改
    this.ready_func_ = null;
    this.callbacks_ = new HashTable();
    this.module_ = "JS_IActiveXCtrl";
    if ('WebSocket' in window) {
        this.ws = new WebSocket("ws://127.0.0.1:7321");
    } else {
        throw "WebSocket not supported";
    }
    this.ws.onerror = function () {
        throw "Unable to establish connection to WebSocket";
    }

    this.loadModule = function () {
        var msg = JSON.stringify({
            "MsgId": "LoadModule",
            "Module": this.module_
        });
        this.ws.onmessage = this._callback.bind(this);
        this.ws.send(msg);
    };

    this.loadModuleCallBack = function (result, response) {
        if (!result)
            throw result.Response;
        if (this.ready_func_) this.ready_func_();
    }.bind(this);

    this.callbacks_.add("LoadModule", this.loadModuleCallBack);

    this.exec = function (func, param, callback) {
        var msg_id = Object.id(callback).toString();
        this.callbacks_.add(msg_id, callback);
        var param_ = {
            "MsgId": msg_id,
            "Method": func + "|" + clsid
        }
        if (param) {
            param_["Param"] = JSON.stringify(param);
        }
        var msg = JSON.stringify(param_);
        this.ws.send(msg);
    }

    this.ready = function (func) {
        this.ready_func_ = func;
    }

    this._callback = function (response) {
        var r = JSON.parse(response.data);
        var msg_id = r.MsgId;
        // if (r.Result) {
        //     console.info(r.Response);
        // } else {
        //     console.error(r.Response);
        // }
        if (!this.callbacks_.containsKey(msg_id)) {
            return;
        }
        // console.log(this.callbacks_.getKeys());
        // console.log(msg_id);
        var callback = this.callbacks_.getValue(msg_id);
        callback(r.Result, r.Response);
        this.callbacks_.remove(msg_id);
    }

    this.ws.onopen = this.loadModule.bind(this);

    /** 
   	* 调用控件接口的部分
   	* 与test.html的调用名称相对应，以下均为示例
  	* exec第一个参数是 ActiveX控件 的接口名称，第二个参数是ActiveX控件的 参数 传入，第三个参数不需要修改
  	* 对每一个ActiveX控件接口都要写一个通过WebSocket调用的函数
  	*/

    this.ET99_FindToken = function (pid, callback) {
        this.exec("ET99_FindToken", [pid], callback);
    }
    this.ET99_OpenToken = function (pid, callback) {
        this.exec("ET99_OpenToken", [pid, 1], callback);
    }
    this.ET99_GetSN = function (callback) {
        this.exec("ET99_GetSN", null, callback);
    }
    this.ET99_Verify = function (callback) {
        this.exec("ET99_Verify", [0, 'FFFFFFFFFFFFFFFF'], callback);
    }
    this.ET99_GenRandom = function (callback) {
        this.exec("ET99_GenRandom", null, callback);
    }
    this.ET99_SetKey = function (callback) {
        this.exec("ET99_SetKey", [parseInt(keyid), keybuff], callback);
    }
    this.ET99_HMAC_MD5 = function (callback) {
        this.exec("ET99_HMAC_MD5", [parseInt(keyid), md5input.length, md5input], callback);
    }
    this.ET99_CloseToken = function (callback) {
        this.exec("ET99_CloseToken", null, callback);
    }
}