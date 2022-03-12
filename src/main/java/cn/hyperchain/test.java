package cn.hyperchain;

import cn.hyperchain.business.SerialTool;
import cn.hyperchain.view.Panel;
import org.junit.Test;

public class test {

    private static SerialTool serialTool;

    @Test
    public void test() {

        serialTool = new SerialTool();

        cn.hyperchain.view.Panel p = new Panel(serialTool);

        while(true){}
    }
}
