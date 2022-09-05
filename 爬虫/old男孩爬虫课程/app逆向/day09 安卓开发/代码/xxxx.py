import importlib


def run():
    # card = "card.cloud"
    card_path = "card.seven"
    m = importlib.import_module(card_path)

    # 1.requests发请求，去卡商获取手机号   1块钱  2块 【手机号】
    # phone = requests.get(".....")
    m.get_number()
    getattr(m,'get_number')()

    # 2.注册平台

    # 3.接收短信验证码【验证码】
    m.get_code(123)
    getattr(m, 'get_code')(123)

    # 4.写入文件


if __name__ == '__main__':
    run()
