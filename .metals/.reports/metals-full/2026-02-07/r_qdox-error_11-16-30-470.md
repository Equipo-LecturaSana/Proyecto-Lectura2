error id: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/service/PedidoService.java
file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/service/PedidoService.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[11,1]

error in qdox parser
file content:
```java
offset: 338
uri: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/service/PedidoService.java
text:
```scala
package com.example.LecturaSana.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.LecturaSana.model.CarritoItem;
import com.example.LecturaSana.model.Pedido;
import com.example.LecturaSana.repository.PedidoRepo
s@@itory;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LibroService libroService;

    public PedidoService(PedidoRepository pedidoRepository, LibroService libroService) {
        this.pedidoRepository = pedidoRepository;
        this.libroService = libroService;
    }

    public List<Pedido> getPedidos() {
        return pedidoRepository.findAll(); 
    }

    @Transactional
    public void guardarPedido(Pedido pedido) {
        // Guardar el pedido primero
        pedidoRepository.save(pedido);
        
        // Actualizar el stock de cada libro en el pedido
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            for (CarritoItem item : pedido.getItems()) {
                try {
                    libroService.reducirStock(item.getLibroId(), item.getCantidad());
                } catch (Exception e) {
                    System.err.println("Error al actualizar stock para libro ID " + item.getLibroId() + ": " + e.getMessage());
                    // Continuamos con los demás items incluso si hay error en uno
                }
            }
        }
    }

    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}
```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:936)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	java.base/java.lang.Thread.run(Thread.java:840)
```
#### Short summary: 

QDox parse error in file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/Lectura%20Sana%20xd/LecturaSana/src/main/java/com/example/LecturaSana/service/PedidoService.java