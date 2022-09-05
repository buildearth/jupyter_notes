from flask import Flask, request, jsonify

app = Flask(__name__)


@app.route('/auth', methods=['POST'])
def auth():
    print(request.form)
    # 1.获取各个数据
    user = request.form.get("user")
    pwd = request.form.get("pwd")
    sign = request.form.get('sign')

    # 2.sign签名的校验

    # 3.根据用户名和密码去数据库校验
    return jsonify({'status': True, 'token': "b96efd24-e323-4efd-8813-659570619cde"})


if __name__ == '__main__':
    # 电脑的IP
    app.run(host='192.168.43.252')
